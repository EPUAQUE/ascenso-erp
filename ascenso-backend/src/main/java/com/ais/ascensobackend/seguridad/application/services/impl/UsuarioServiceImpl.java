package com.ais.ascensobackend.seguridad.application.services.impl;

import com.ais.ascensobackend.destacamentos.application.dtos.DestacamentoResumen;
import com.ais.ascensobackend.destacamentos.application.services.interfaces.DestacamentoService;
import com.ais.ascensobackend.destacamentos.domain.model.Destacamento;
import com.ais.ascensobackend.destacamentos.domain.repository.DestacamentoRepository;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioDestacamentoResumen;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioNombreResumen;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioResumen;
import com.ais.ascensobackend.seguridad.application.services.interfaces.AutorizacionDestacamentoService;
import com.ais.ascensobackend.seguridad.application.services.interfaces.UsuarioService;
import com.ais.ascensobackend.seguridad.domain.exception.PasswordActualInvalidaException;
import com.ais.ascensobackend.seguridad.domain.exception.UsuarioDuplicadoException;
import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.domain.model.Usuario;
import com.ais.ascensobackend.seguridad.domain.model.UsuarioDestacamento;
import com.ais.ascensobackend.seguridad.domain.repository.RefreshTokenRepository;
import com.ais.ascensobackend.seguridad.domain.repository.RolRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioDestacamentoRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.seguridad.domain.service.PermisosEfectivosResolver;
import com.ais.ascensobackend.seguridad.domain.service.PoliticaContrasenaValidator;
import com.ais.ascensobackend.seguridad.domain.service.SecurityAuditPublisher;
import com.ais.ascensobackend.seguridad.domain.service.TemporaryPasswordGenerator;
import com.ais.ascensobackend.seguridad.domain.service.TipoEventoAuditoria;
import com.ais.ascensobackend.seguridad.domain.service.UsernameCanonicalizer;
import com.ais.ascensobackend.seguridad.infrastructure.security.SeguridadProperties;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioDestacamentoRepository usuarioDestacamentoRepository;
    private final RolRepository rolRepository;
    private final DestacamentoRepository destacamentoRepository;
    private final DestacamentoService destacamentoService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermisosEfectivosResolver permisosEfectivosResolver;
    private final SecurityAuditPublisher auditPublisher;
    private final SeguridadProperties properties;
    private final AutorizacionDestacamentoService autorizacionDestacamentoService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            UsuarioDestacamentoRepository usuarioDestacamentoRepository,
            RolRepository rolRepository,
            DestacamentoRepository destacamentoRepository,
            DestacamentoService destacamentoService,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            PermisosEfectivosResolver permisosEfectivosResolver,
            SecurityAuditPublisher auditPublisher,
            SeguridadProperties properties,
            AutorizacionDestacamentoService autorizacionDestacamentoService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioDestacamentoRepository = usuarioDestacamentoRepository;
        this.rolRepository = rolRepository;
        this.destacamentoRepository = destacamentoRepository;
        this.destacamentoService = destacamentoService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.permisosEfectivosResolver = permisosEfectivosResolver;
        this.auditPublisher = auditPublisher;
        this.properties = properties;
        this.autorizacionDestacamentoService = autorizacionDestacamentoService;
    }

    @Override
    @Transactional
    public UsuarioResumen crear(
            String username, String passwordPlano, String nombre, String telefono, String correo) {
        String usernameCanonico = UsernameCanonicalizer.canonicalizar(username);
        PoliticaContrasenaValidator.validar(
                passwordPlano, properties.passwordPolicy().minLength(), properties.passwordPolicy().maxLength());

        if (usuarioRepository.existsByUsername(usernameCanonico)) {
            throw new UsuarioDuplicadoException(usernameCanonico);
        }

        Usuario usuario = Usuario.nuevo(
                usernameCanonico, passwordEncoder.encode(passwordPlano), nombre, telefono, correo);
        Usuario guardado = usuarioRepository.save(usuario);
        auditPublisher.publicar(
                TipoEventoAuditoria.USUARIO_CREADO, UUID.randomUUID().toString(), "usuarioId=" + guardado.getId());
        return toResumen(guardado);
    }

    @Override
    @Transactional
    public void asignarDestacamento(Long usuarioId, Long destacamentoId, Long rolId) {
        Rol rol = validarUsuarioYRolParaAsignacion(usuarioId, rolId);
        Destacamento destacamento = destacamentoRepository.findById(destacamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Destacamento no encontrado: " + destacamentoId));
        autorizacionDestacamentoService.exigirAcceso(destacamentoId);
        exigirNoEscalaAlcanceGlobal(rol);
        ejecutarAsignacionDestacamento(usuarioId, destacamentoId, rolId, rol, destacamento);
    }

    @Override
    @Transactional
    public void asignarDestacamentoSistema(Long usuarioId, Long destacamentoId, Long rolId) {
        Rol rol = validarUsuarioYRolParaAsignacion(usuarioId, rolId);
        Destacamento destacamento = destacamentoRepository.findById(destacamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Destacamento no encontrado: " + destacamentoId));
        ejecutarAsignacionDestacamento(usuarioId, destacamentoId, rolId, rol, destacamento);
    }

    private Rol validarUsuarioYRolParaAsignacion(Long usuarioId, Long rolId) {
        usuarioRepository.findByIdConBloqueo(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        return rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + rolId));
    }

    private void ejecutarAsignacionDestacamento(
            Long usuarioId, Long destacamentoId, Long rolId, Rol rol, Destacamento destacamento) {
        usuarioDestacamentoRepository.save(new UsuarioDestacamento(null, usuarioId, destacamentoId, rol));
        permisosEfectivosResolver.invalidar(usuarioId);
        auditPublisher.publicar(TipoEventoAuditoria.DESTACAMENTO_ASIGNADO, UUID.randomUUID().toString(),
                "usuarioId=" + usuarioId + ",destacamentoId=" + destacamentoId + ",rolId=" + rolId);
    }

    @Override
    @Transactional
    public void asignarRolGlobalSistema(Long usuarioId, Long rolId) {
        Rol rol = validarUsuarioYRolParaAsignacion(usuarioId, rolId);
        usuarioDestacamentoRepository.save(new UsuarioDestacamento(null, usuarioId, null, rol));
        permisosEfectivosResolver.invalidar(usuarioId);
        auditPublisher.publicar(TipoEventoAuditoria.ROL_GLOBAL_ASIGNADO, UUID.randomUUID().toString(),
                "usuarioId=" + usuarioId + ",rolId=" + rolId);
    }

    @Override
    public List<UsuarioDestacamentoResumen> listarDestacamentos(Long usuarioId) {
        return usuarioDestacamentoRepository.findByUsuarioId(usuarioId).stream()
                .map(ud -> new UsuarioDestacamentoResumen(
                        ud.getId(), ud.getDestacamentoId(), ud.getRol().getId(), ud.getRol().getNombre()))
                .toList();
    }

    @Override
    public UsuarioResumen obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(this::toResumen)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public PermisosEfectivos obtenerPermisosEfectivos(Long usuarioId) {
        return permisosEfectivosResolver.resolver(usuarioId);
    }

    @Override
    public PermisosEfectivos obtenerPermisosEfectivosPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return permisosEfectivosResolver.resolver(usuario.getId());
    }

    @Override
    public List<UsuarioResumen> listar() {
        Optional<Set<Long>> destacamentoIdsPermitidos = autorizacionDestacamentoService.destacamentoIdsPermitidos();
        if (destacamentoIdsPermitidos.isEmpty()) {
            return usuarioRepository.findAll().stream().map(this::toResumen).toList();
        }
        Set<Long> usuarioIdsEnAlcance = new HashSet<>(
                usuarioDestacamentoRepository.listarUsuarioIdsPorDestacamentos(destacamentoIdsPermitidos.get()));

        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuarioIdsEnAlcance.contains(usuario.getId()))
                .map(this::toResumen)
                .toList();
    }

    @Override
    @Transactional
    public void cambiarMiPassword(Long usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw new PasswordActualInvalidaException();
        }
        PoliticaContrasenaValidator.validar(
                passwordNueva, properties.passwordPolicy().minLength(), properties.passwordPolicy().maxLength());

        usuario.cambiarPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.PASSWORD_CAMBIADA, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
    }

    @Override
    @Transactional
    public String restablecerPassword(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));

        String passwordTemporal = TemporaryPasswordGenerator.generar();
        usuario.restablecerConPasswordTemporal(passwordEncoder.encode(passwordTemporal));
        usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.PASSWORD_RESTABLECIDA, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
        return passwordTemporal;
    }

    @Override
    @Transactional
    public void revocarSesiones(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        usuario.revocarSesiones();
        usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.SESIONES_REVOCADAS, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
    }

    @Override
    @Transactional
    public UsuarioResumen desactivar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        usuario.desactivar();
        Usuario guardado = usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuarioId);
        permisosEfectivosResolver.invalidar(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.USUARIO_DESACTIVADO, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
        return toResumen(guardado);
    }

    @Override
    @Transactional
    public UsuarioResumen bloquear(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        usuario.bloquear();
        Usuario guardado = usuarioRepository.save(usuario);
        refreshTokenRepository.revocarTodosDeUsuario(usuarioId);
        permisosEfectivosResolver.invalidar(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.USUARIO_BLOQUEADO, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
        return toResumen(guardado);
    }

    @Override
    @Transactional
    public UsuarioResumen activar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        usuario.activar();
        Usuario guardado = usuarioRepository.save(usuario);
        permisosEfectivosResolver.invalidar(usuarioId);
        auditPublisher.publicar(
                TipoEventoAuditoria.USUARIO_ACTIVADO, UUID.randomUUID().toString(), "usuarioId=" + usuarioId);
        return toResumen(guardado);
    }

    @Override
    public List<UsuarioNombreResumen> listarNombres(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return usuarioRepository.findAllById(ids).stream()
                .map(usuario -> new UsuarioNombreResumen(usuario.getId(), usuario.getNombre()))
                .toList();
    }

    @Override
    public List<DestacamentoResumen> misDestacamentos(String username) {
        PermisosEfectivos permisos = obtenerPermisosEfectivosPorUsername(username);
        if (permisos.alcanceGlobal()) {
            return destacamentoService.listar();
        }
        return destacamentoService.listarPorIds(permisos.destacamentoIds());
    }

    private void exigirNoEscalaAlcanceGlobal(Rol rol) {
        if (rol.isAlcanceGlobal() && autorizacionDestacamentoService.destacamentoIdsPermitidos().isPresent()) {
            throw new AccessDeniedException("No autorizado a asignar un rol de alcance global: " + rol.getNombre());
        }
    }

    private UsuarioResumen toResumen(Usuario usuario) {
        return new UsuarioResumen(
                usuario.getId(), usuario.getUsername(), usuario.getEstado(), usuario.getNombre(),
                usuario.getTelefono(), usuario.getCorreo());
    }
}
