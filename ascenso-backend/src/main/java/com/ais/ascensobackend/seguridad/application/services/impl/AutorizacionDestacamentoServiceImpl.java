package com.ais.ascensobackend.seguridad.application.services.impl;

import com.ais.ascensobackend.seguridad.application.services.interfaces.AutorizacionDestacamentoService;
import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.service.ContextoAutenticacion;
import com.ais.ascensobackend.seguridad.domain.service.PermisosEfectivosResolver;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AutorizacionDestacamentoServiceImpl implements AutorizacionDestacamentoService {

    private final ContextoAutenticacion contextoAutenticacion;
    private final PermisosEfectivosResolver permisosEfectivosResolver;

    public AutorizacionDestacamentoServiceImpl(
            ContextoAutenticacion contextoAutenticacion, PermisosEfectivosResolver permisosEfectivosResolver) {
        this.contextoAutenticacion = contextoAutenticacion;
        this.permisosEfectivosResolver = permisosEfectivosResolver;
    }

    @Override
    public void exigirAcceso(Long destacamentoId) {
        if (!tieneAcceso(destacamentoId)) {
            throw new AccessDeniedException("Destacamento fuera de alcance: " + destacamentoId);
        }
    }

    @Override
    public void exigirAccesoATodos(Collection<Long> destacamentoIds) {
        destacamentoIds.forEach(this::exigirAcceso);
    }

    @Override
    public boolean tieneAcceso(Long destacamentoId) {
        PermisosEfectivos permisos = permisosEfectivosResolver.resolver(contextoAutenticacion.usuarioIdActual());
        return permisos.puedeAccederADestacamento(destacamentoId);
    }

    @Override
    public Optional<Set<Long>> destacamentoIdsPermitidos() {
        PermisosEfectivos permisos = permisosEfectivosResolver.resolver(contextoAutenticacion.usuarioIdActual());
        return permisos.alcanceGlobal() ? Optional.empty() : Optional.of(permisos.destacamentoIds());
    }
}
