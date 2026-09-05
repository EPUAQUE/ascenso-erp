package com.ais.ascensobackend.actividades.infrastructure.persistence.entities;

import java.io.Serializable;
import java.util.Objects;
import lombok.NoArgsConstructor;

/** Clave compuesta de {@link NinoPadreEntity} — ver {@code @IdClass} en esa entidad. */
@NoArgsConstructor
public class NinoPadreId implements Serializable {

    private Long ninoId;
    private Long usuarioId;

    public NinoPadreId(Long ninoId, Long usuarioId) {
        this.ninoId = ninoId;
        this.usuarioId = usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NinoPadreId that)) return false;
        return Objects.equals(ninoId, that.ninoId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ninoId, usuarioId);
    }
}
