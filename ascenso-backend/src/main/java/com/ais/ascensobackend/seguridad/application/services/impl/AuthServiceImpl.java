package com.ais.ascensobackend.seguridad.application.services.impl;

import com.ais.ascensobackend.seguridad.application.dtos.LoginResult;
import com.ais.ascensobackend.seguridad.application.services.interfaces.AuthService;
import com.ais.ascensobackend.seguridad.domain.exception.AutenticacionFallidaException;
import com.ais.ascensobackend.seguridad.domain.exception.TokenResetInvalidoException;
import com.ais.ascensobackend.seguridad.domain.model.PasswordResetToken;
import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.model.RefreshToken;
import com.ais.ascensobackend.seguridad.domain.model.Usuario;
import com.ais.ascensobackend.seguridad.domain.repository.PasswordResetTokenRepository;
import com.ais.ascensobackend.seguridad.domain.repository.RefreshTokenRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.seguridad.domain.service.AccessTokenIssuer;
import com.ais.ascensobackend.seguridad.domain.service.LoginRateLimiter;
import com.ais.ascensobackend.seguridad.domain.service.PasswordResetMailSender;
import com.ais.ascensobackend.seguridad.domain.service.PermisosEfectivosResolver;
import com.ais.ascensobackend.seguridad.domain.service.PoliticaContrasenaValidator;
import com.ais.ascensobackend.seguridad.domain.service.RefreshTokenCrypto;
import com.ais.ascensobackend.seguridad.domain.service.SecurityAuditPublisher;
import com.ais.ascensobackend.seguridad.domain.service.TipoEventoAuditoria;
import com.ais.ascensobackend.seguridad.domain.service.UsernameCanonicalizer;
import com.ais.ascensobackend.seguridad.infrastructure.security.SeguridadProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetMailSender passwordResetMailSender;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenIssuer accessTokenIssuer;
    private final PermisosEfectivosResolver permisosEfectivosResolver;
    private final LoginRateLimiter loginRateLimiter;
    private final SecurityAuditPublisher auditPublisher;
    private final SeguridadProperties properties;
    private final Duration refreshTokenTtl;
    private final Duration passwordResetTokenTtl;

    /** Hash Argon2id de una contraseña ficticia, para ejecutar matches() aun sin usuario real. */
    private final String credencialFicticia;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetMailSender passwordResetMailSender,
            PasswordEncoder passwordEncoder,
            AccessTokenIssuer accessTokenIssuer,
            PermisosEfectivosResolver permisosEfectivosResolver,
            LoginRateLimiter loginRateLimiter,
            SecurityAuditPublisher auditPublisher,
            SeguridadProperties properties) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetMailSender = passwordResetMailSender;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenIssuer = accessTokenIssuer;
        this.permisosEfectivosResolver = permisosEfectivosResolver;
        this.loginRateLimiter = loginRateLimiter;
        this.auditPublisher = auditPublisher;
        this.properties = properties;
        this.refreshTokenTtl = properties.refreshToken().ttl();
        this.passwordResetTokenTtl = properties.passwordReset() != null
                ? properties.passwordReset().ttl() : Duration.ofMinutes(30);
        this.credencialFicticia = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Override
    @Transactional
    public LoginResult login(String username, String passwordPlano, String claveIp) {
        String correlationId = UUID.randomUUID().toString();
        String usernameCanonico = UsernameCanonicalizer.canonicalizar(username);
        String usernameHash = RefreshTokenCrypto.hash(usernameCanonico);

        loginRateLimiter.verificarPermitido(claveIp, usernameHash);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usernameCanonico);
        String hashParaVerificar = usuarioOpt.map(Usuario::getPasswordHash).orElse(credencialFicticia);
        boolean coincide = passwordEncoder.matches(passwordPlano, hashParaVerificar);

        if (usuarioOpt.isEmpty() || !coincide || !usuarioOpt.get().estaActivo()) {
            auditPublisher.publicar(TipoEventoAuditoria.LOGIN_FALLIDO, correlationId, "login=" + usernameHash);
            throw new AutenticacionFallidaException();
        }

        Usuario usuario = usuarioOpt.get();
        PermisosEfectivos permisos = permisosEfectivosResolver.resolver(usuario.getId());
        exigirAlcanceOAuditarYRechazar(usuario.getId(), permisos, correlationId, TipoEventoAuditoria.LOGIN_FALLIDO);
        LoginResult resultado = emitirTokens(usuario, permisos);
        auditPublisher.publicar(TipoEventoAuditoria.LOGIN_EXITOSO, correlationId, "usuarioId=" + usuario.getId());
        return resultado;
    }

    /**
     * Un rol de alcance global (SUPERVISOR_GENERAL) no necesita destacamento
     * asignado; cualquier otro rol sí. Mismo mensaje/código genérico que cualquier
     * otra falla de autenticación.
     */
    private void exigirAlcanceOAuditarYRechazar(
            Long usuarioId, PermisosEfectivos permisos, String correlationId, TipoEventoAuditoria tipoEventoFallo) {
        if (!permisos.alcanceGlobal() && permisos.destacamentoIds().isEmpty()) {
            auditPublisher.publicar(
                    tipoEventoFallo, correlationId, "usuarioId=" + usuarioId + ",motivo=sin_destacamento_asignado");
            throw new AutenticacionFallidaException();
        }
    }

    /**
     * Consume el token con un {@code UPDATE} condicional atómico
     * ({@link RefreshTokenRepository#consumir}) en vez de la secuencia no protegida
     * find→comprobar→save. La rama perdedora (0 filas actualizadas) siempre se trata
     * como reutilización y revoca toda la familia.
     *
     * {@code noRollbackFor}: sin esto, la revocación de familia hecha en
     * {@code manejarConsumoFallido} antes de lanzar {@code AutenticacionFallidaException}
     * se deshace por el rollback por defecto de Spring.
     */
    @Override
    @Transactional(noRollbackFor = AutenticacionFallidaException.class)
    public LoginResult refresh(String refreshTokenPlano) {
        String correlationId = UUID.randomUUID().toString();
        String hash = RefreshTokenCrypto.hash(refreshTokenPlano);
        Instant ahora = Instant.now();

        boolean consumido = refreshTokenRepository.consumir(hash, ahora) == 1;
        if (!consumido) {
            manejarConsumoFallido(hash, correlationId);
        }

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(AutenticacionFallidaException::new);
        Usuario usuario = usuarioRepository.findById(token.getUsuarioId())
                .filter(Usuario::estaActivo)
                .orElseThrow(AutenticacionFallidaException::new);

        PermisosEfectivos permisos = permisosEfectivosResolver.resolver(usuario.getId());
        exigirAlcanceOAuditarYRechazar(usuario.getId(), permisos, correlationId, TipoEventoAuditoria.REFRESH_FALLIDO);
        LoginResult resultado = emitirTokensRotados(usuario, permisos, token.getId());
        auditPublisher.publicar(TipoEventoAuditoria.REFRESH_EXITOSO, correlationId, "usuarioId=" + usuario.getId());
        return resultado;
    }

    private void manejarConsumoFallido(String hash, String correlationId) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(hash);
        if (tokenOpt.isEmpty()) {
            auditPublisher.publicar(TipoEventoAuditoria.REFRESH_FALLIDO, correlationId, "token no encontrado");
            throw new AutenticacionFallidaException();
        }

        RefreshToken token = tokenOpt.get();
        if (token.isRevocado()) {
            refreshTokenRepository.revocarTodosDeUsuario(token.getUsuarioId());
            auditPublisher.publicar(
                    TipoEventoAuditoria.REFRESH_REUTILIZADO, correlationId, "usuarioId=" + token.getUsuarioId());
            throw new AutenticacionFallidaException();
        }

        auditPublisher.publicar(TipoEventoAuditoria.REFRESH_FALLIDO, correlationId, "token expirado");
        throw new AutenticacionFallidaException();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenPlano) {
        String hash = RefreshTokenCrypto.hash(refreshTokenPlano);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.revocar();
            refreshTokenRepository.save(token);
            auditPublisher.publicar(TipoEventoAuditoria.LOGOUT, UUID.randomUUID().toString(),
                    "usuarioId=" + token.getUsuarioId());
        });
    }

    private LoginResult emitirTokens(Usuario usuario, PermisosEfectivos permisos) {
        return emitirTokensRotados(usuario, permisos, null);
    }

    private LoginResult emitirTokensRotados(Usuario usuario, PermisosEfectivos permisos, Long tokenPadreId) {
        AccessTokenIssuer.Resultado accessToken = accessTokenIssuer.emitir(usuario, permisos);

        String refreshPlano = RefreshTokenCrypto.generarOpaco();
        Instant ahora = Instant.now();
        RefreshToken nuevoRefresh = RefreshToken.nuevo(
                usuario.getId(), RefreshTokenCrypto.hash(refreshPlano), ahora, ahora.plus(refreshTokenTtl), tokenPadreId);
        refreshTokenRepository.save(nuevoRefresh);

        long expiresInSeconds = Duration.between(ahora, accessToken.expiraEn()).toSeconds();
        return new LoginResult(accessToken.token(), refreshPlano, expiresInSeconds, usuario.isDebeCambiarPassword());
    }

    /**
     * Nunca lanza excepción por usuario inexistente/sin correo/inactivo — mismo
     * criterio de no enumeración que {@link #login}. Reusa {@link LoginRateLimiter}
     * (por IP y por hash del username canónico) para frenar abuso de envío de correo.
     */
    @Override
    @Transactional
    public void solicitarRestablecimiento(String username, String claveIp) {
        String correlationId = UUID.randomUUID().toString();
        String usernameCanonico = UsernameCanonicalizer.canonicalizar(username);
        String usernameHash = RefreshTokenCrypto.hash(usernameCanonico);

        loginRateLimiter.verificarPermitido(claveIp, usernameHash);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usernameCanonico);
        if (usuarioOpt.isEmpty()) {
            log.info("Solicitud de restablecimiento de contraseña para un username no encontrado.");
            auditPublisher.publicar(
                    TipoEventoAuditoria.PASSWORD_RESET_SOLICITADO, correlationId, "motivo=usuario_no_encontrado");
            return;
        }

        Usuario usuario = usuarioOpt.get();
        boolean elegible = usuario.estaActivo() && usuario.getCorreo() != null && !usuario.getCorreo().isBlank();
        if (!elegible) {
            log.info("Solicitud de restablecimiento de contraseña no procesada para usuarioId={} "
                    + "(inactivo/bloqueado o sin correo registrado).", usuario.getId());
            auditPublisher.publicar(TipoEventoAuditoria.PASSWORD_RESET_SOLICITADO, correlationId,
                    "usuarioId=" + usuario.getId() + ",motivo=no_elegible");
            return;
        }

        passwordResetTokenRepository.invalidarNoUsadosDeUsuario(usuario.getId());
        String tokenPlano = RefreshTokenCrypto.generarOpaco();
        Instant ahora = Instant.now();
        PasswordResetToken token = PasswordResetToken.nuevo(
                usuario.getId(), RefreshTokenCrypto.hash(tokenPlano), ahora, ahora.plus(passwordResetTokenTtl));
        passwordResetTokenRepository.save(token);

        passwordResetMailSender.enviar(usuario.getCorreo(), tokenPlano);
        auditPublisher.publicar(
                TipoEventoAuditoria.PASSWORD_RESET_SOLICITADO, correlationId, "usuarioId=" + usuario.getId());
    }

    /**
     * Valida la política de contraseña ANTES de consumir el token — así una
     * contraseña que no cumple la política no quema el token de un solo uso.
     */
    @Override
    @Transactional
    public void restablecerPassword(String tokenPlano, String passwordNueva) {
        String correlationId = UUID.randomUUID().toString();
        PoliticaContrasenaValidator.validar(
                passwordNueva, properties.passwordPolicy().minLength(), properties.passwordPolicy().maxLength());

        String hash = RefreshTokenCrypto.hash(tokenPlano);
        boolean consumido = passwordResetTokenRepository.consumir(hash, Instant.now()) == 1;
        if (!consumido) {
            auditPublisher.publicar(TipoEventoAuditoria.PASSWORD_RESET_FALLIDO, correlationId,
                    "motivo=token_invalido_o_expirado");
            throw new TokenResetInvalidoException();
        }

        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(TokenResetInvalidoException::new);
        Usuario usuario = usuarioRepository.findById(token.getUsuarioId())
                .orElseThrow(TokenResetInvalidoException::new);

        usuario.cambiarPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuario.getId());

        auditPublisher.publicar(
                TipoEventoAuditoria.PASSWORD_RESET_EXITOSO, correlationId, "usuarioId=" + usuario.getId());
    }
}
