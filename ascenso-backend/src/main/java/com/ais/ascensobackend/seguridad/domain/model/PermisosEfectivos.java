package com.ais.ascensobackend.seguridad.domain.model;

import java.util.Set;

/**
 * Autorización efectiva de un usuario: unión de permisos de todos sus roles y
 * destacamentos asignados. Esta fase solo soporta asignación individual de
 * destacamento (sin grupos de destacamentos, a diferencia del proyecto de
 * referencia market-backend).
 */
public record PermisosEfectivos(
        Long usuarioId, String username, Set<String> permisos, Set<Long> destacamentoIds, boolean alcanceGlobal) {

    public boolean tienePermiso(String codigo) {
        return permisos.contains(codigo);
    }

    public boolean puedeAccederADestacamento(Long destacamentoId) {
        return alcanceGlobal || destacamentoIds.contains(destacamentoId);
    }
}
