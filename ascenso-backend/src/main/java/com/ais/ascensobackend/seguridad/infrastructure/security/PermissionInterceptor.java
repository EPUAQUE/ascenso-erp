package com.ais.ascensobackend.seguridad.infrastructure.security;

import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.seguridad.domain.service.PermisosEfectivosResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Aplica {@link RequiresPermission}: exige el permiso declarado y, si el endpoint
 * expone una variable de ruta {@code destacamentoId}, el alcance de destacamento
 * del usuario. Endpoints sin la anotación quedan autenticados (Spring Security),
 * pero sin autorización de negocio adicional.
 */
public class PermissionInterceptor implements HandlerInterceptor {

    private final UsuarioRepository usuarioRepository;
    private final PermisosEfectivosResolver permisosEfectivosResolver;

    public PermissionInterceptor(
            UsuarioRepository usuarioRepository, PermisosEfectivosResolver permisosEfectivosResolver) {
        this.usuarioRepository = usuarioRepository;
        this.permisosEfectivosResolver = permisosEfectivosResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresPermission anotacion = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        if (anotacion == null) {
            anotacion = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
        }
        if (anotacion == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No autenticado.");
        }

        String username = authentication.getName();
        Long usuarioId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Usuario no reconocido."))
                .getId();
        PermisosEfectivos permisos = permisosEfectivosResolver.resolver(usuarioId);

        if (!permisos.tienePermiso(anotacion.value())) {
            throw new AccessDeniedException("Permiso requerido: " + anotacion.value());
        }

        Long destacamentoId = extraerDestacamentoId(request);
        if (destacamentoId != null && !permisos.puedeAccederADestacamento(destacamentoId)) {
            throw new AccessDeniedException("Destacamento fuera de alcance: " + destacamentoId);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Long extraerDestacamentoId(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(attribute instanceof Map<?, ?> variables)) {
            return null;
        }
        Object valor = ((Map<String, String>) variables).get("destacamentoId");
        return valor == null ? null : Long.valueOf(valor.toString());
    }
}
