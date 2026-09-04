package com.ais.ascensobackend.seguridad.domain.service;

import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;

/**
 * Resuelve la autorización efectiva de un usuario (unión de permisos de sus roles +
 * destacamentos asignados). La implementación puede cachear con TTL corto; el
 * dominio solo exige que sea consistente con la base de datos como fuente de verdad.
 */
public interface PermisosEfectivosResolver {

    PermisosEfectivos resolver(Long usuarioId);

    /** Invalida la entrada cacheada de un usuario tras un cambio crítico (rol, destacamento, estado). */
    void invalidar(Long usuarioId);
}
