package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.ProgresoLiderazgoFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} completó un {@code Liderazgo} (catálogo)
 * rumbo a un año de programa objetivo. Un niño completa cada liderazgo a lo
 * sumo una vez (ver {@code ux_progreso_liderazgo_nino_liderazgo}). Histórico:
 * no se edita, solo se crea.
 */
public class ProgresoLiderazgo {

    private final Long id;
    private final Long ninoId;
    private final Long liderazgoId;
    private final Long anioProgramaObjetivoId;
    private final LocalDate fechaCompletado;
    private final Long registradoPor;

    public ProgresoLiderazgo(
            Long id, Long ninoId, Long liderazgoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
            Long registradoPor) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.liderazgoId = Objects.requireNonNull(liderazgoId, "liderazgoId");
        this.anioProgramaObjetivoId = Objects.requireNonNull(anioProgramaObjetivoId, "anioProgramaObjetivoId");
        this.fechaCompletado = requerirNoFutura(fechaCompletado);
        this.registradoPor = Objects.requireNonNull(registradoPor, "registradoPor");
    }

    public static ProgresoLiderazgo nuevo(
            Long ninoId, Long liderazgoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado, Long registradoPor) {
        return new ProgresoLiderazgo(null, ninoId, liderazgoId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaCompletado) {
        Objects.requireNonNull(fechaCompletado, "fechaCompletado");
        if (fechaCompletado.isAfter(LocalDate.now())) {
            throw new ProgresoLiderazgoFechaInvalidaException(fechaCompletado);
        }
        return fechaCompletado;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getLiderazgoId() {
        return liderazgoId;
    }

    public Long getAnioProgramaObjetivoId() {
        return anioProgramaObjetivoId;
    }

    public LocalDate getFechaCompletado() {
        return fechaCompletado;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }
}
