package com.ais.ascensobackend.catalogo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Regla vigente de asistencia mínima requerida (ej. 75% de sesiones por
 * trimestre). Histórica: cada nueva regla creada queda con su propia
 * {@code vigenteDesde}; la regla "actual" es la de fecha más reciente que ya
 * inició.
 */
public class ReglaAsistencia {

    private final Long id;
    private UnidadAsistencia unidad;
    private BigDecimal minimoRequerido;
    private LocalDate vigenteDesde;

    public ReglaAsistencia(Long id, UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
        this.id = id;
        this.unidad = Objects.requireNonNull(unidad, "unidad");
        this.minimoRequerido = Objects.requireNonNull(minimoRequerido, "minimoRequerido");
        this.vigenteDesde = Objects.requireNonNull(vigenteDesde, "vigenteDesde");
    }

    public static ReglaAsistencia nueva(UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
        return new ReglaAsistencia(null, unidad, minimoRequerido, vigenteDesde);
    }

    public void actualizarDatos(UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
        this.unidad = Objects.requireNonNull(unidad, "unidad");
        this.minimoRequerido = Objects.requireNonNull(minimoRequerido, "minimoRequerido");
        this.vigenteDesde = Objects.requireNonNull(vigenteDesde, "vigenteDesde");
    }

    public Long getId() {
        return id;
    }

    public UnidadAsistencia getUnidad() {
        return unidad;
    }

    public BigDecimal getMinimoRequerido() {
        return minimoRequerido;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }
}
