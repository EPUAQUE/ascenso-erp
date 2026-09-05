package com.ais.ascensobackend.actividades.domain.model;

import java.util.Objects;

/**
 * Vínculo de un usuario con rol PADRE hacia un niño cuyo progreso puede ver —
 * asociación pura (sin identidad propia, clave primaria compuesta
 * {@code nino_id, usuario_id} en base de datos).
 */
public class NinoPadre {

    private final Long ninoId;
    private final Long usuarioId;

    public NinoPadre(Long ninoId, Long usuarioId) {
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
    }

    public static NinoPadre nuevo(Long ninoId, Long usuarioId) {
        return new NinoPadre(ninoId, usuarioId);
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}
