package com.ais.ascensobackend.seguridad.api.controllers;

import com.ais.ascensobackend.destacamentos.api.dtos.responses.DestacamentoResponse;
import com.ais.ascensobackend.destacamentos.api.mappers.DestacamentoApiMapper;
import com.ais.ascensobackend.seguridad.api.dtos.requests.CambiarPasswordRequest;
import com.ais.ascensobackend.seguridad.api.dtos.requests.ForgotPasswordRequest;
import com.ais.ascensobackend.seguridad.api.dtos.requests.LoginRequest;
import com.ais.ascensobackend.seguridad.api.dtos.requests.ResetPasswordRequest;
import com.ais.ascensobackend.seguridad.api.dtos.responses.ForgotPasswordResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.LoginResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.MeResponse;
import com.ais.ascensobackend.seguridad.application.dtos.LoginResult;
import com.ais.ascensobackend.seguridad.application.services.interfaces.AuthService;
import com.ais.ascensobackend.seguridad.application.services.interfaces.UsuarioService;
import com.ais.ascensobackend.seguridad.domain.exception.AutenticacionFallidaException;
import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.infrastructure.security.SeguridadProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * El refresh token viaja en una cookie {@code HttpOnly, Secure, SameSite=Strict}
 * (nunca en el cuerpo JSON ni accesible por JavaScript). {@code SameSite=Strict}
 * es aquí la defensa CSRF primaria para /refresh y /logout.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String COOKIE_REFRESH = "refresh_token";
    private static final String COOKIE_PATH = "/api/v1/auth";
    private static final String MENSAJE_FORGOT_PASSWORD =
            "Si el usuario existe y tiene un correo registrado, se enviará un enlace para restablecer la contraseña.";

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final SeguridadProperties properties;
    private final DestacamentoApiMapper destacamentoMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        LoginResult resultado = authService.login(request.username(), request.password(), clienteIp(http));
        return conCookieDeRefresh(resultado);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest http) {
        String refreshToken = leerCookie(http).orElseThrow(AutenticacionFallidaException::new);
        LoginResult resultado = authService.refresh(refreshToken);
        return conCookieDeRefresh(resultado);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest http) {
        leerCookie(http).ifPresent(authService::logout);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.SET_COOKIE, clearedCookie().toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal Jwt jwt) {
        PermisosEfectivos permisos = usuarioService.obtenerPermisosEfectivosPorUsername(jwt.getSubject());
        return ResponseEntity.ok(MeResponse.builder()
                .username(permisos.username())
                .permisos(permisos.permisos())
                .destacamentoIds(permisos.destacamentoIds())
                .alcanceGlobal(permisos.alcanceGlobal())
                .build());
    }

    /**
     * Autoservicio: cualquier usuario autenticado resuelve el nombre real de sus
     * propios destacamentos (id + nombre), sin requerir {@code DESTACAMENTOS_VER} —
     * un LIDER_PRINCIPAL/LIDER_GRUPO solo conoce sus propios destacamentoIds (ver
     * {@link #me}), nunca sus nombres. Con alcance global devuelve el catálogo
     * completo, igual que {@code DestacamentoController.listar}.
     */
    @GetMapping("/mis-destacamentos")
    public ResponseEntity<List<DestacamentoResponse>> misDestacamentos(@AuthenticationPrincipal Jwt jwt) {
        List<DestacamentoResponse> destacamentos = usuarioService.misDestacamentos(jwt.getSubject()).stream()
                .map(destacamentoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(destacamentos);
    }

    /**
     * Autoservicio: cualquier usuario autenticado cambia su propia contraseña, sin
     * requerir un permiso adicional.
     */
    @PostMapping("/password")
    public ResponseEntity<Void> cambiarMiPassword(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CambiarPasswordRequest request) {
        Long usuarioId = usuarioService.obtenerPorUsername(jwt.getSubject()).id();
        usuarioService.cambiarMiPassword(usuarioId, request.passwordActual(), request.passwordNueva());
        return ResponseEntity.noContent().build();
    }

    /**
     * Público — sin JWT (ver SecurityConfig). Respuesta siempre 200 con el mismo
     * mensaje genérico, exista o no el usuario, tenga o no correo, esté o no activo.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest http) {
        authService.solicitarRestablecimiento(request.username(), clienteIp(http));
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(new ForgotPasswordResponse(MENSAJE_FORGOT_PASSWORD));
    }

    /** Público — sin JWT (ver SecurityConfig). Token de un solo uso emitido por {@link #forgotPassword}. */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.restablecerPassword(request.token(), request.nuevaPassword());
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<LoginResponse> conCookieDeRefresh(LoginResult resultado) {
        LoginResponse body = LoginResponse.builder()
                .accessToken(resultado.accessToken())
                .tokenType("Bearer")
                .expiresIn(resultado.expiresInSeconds())
                .debeCambiarPassword(resultado.debeCambiarPassword())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.SET_COOKIE, refreshCookie(resultado.refreshToken()).toString())
                .body(body);
    }

    private Optional<String> leerCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_REFRESH.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(COOKIE_REFRESH, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(COOKIE_PATH)
                .maxAge(properties.refreshToken().ttl())
                .build();
    }

    private ResponseCookie clearedCookie() {
        return ResponseCookie.from(COOKIE_REFRESH, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    private String clienteIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
