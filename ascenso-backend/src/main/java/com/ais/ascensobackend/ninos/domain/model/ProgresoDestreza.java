package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.ProgresoDestrezaFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} completó una {@code Destreza} (catálogo)
 * rumbo a un año de programa objetivo. Un niño completa cada destreza a lo
 * sumo una vez (ver {@code ux_progreso_destreza_nino_destreza}). Histórico: no
 * se edita, solo se crea.
 */
public class ProgresoDestreza {

    private final Long id;
    private final Long ninoId;
    private final Long destrezaId;
    private final Long anioProgramaObjetivoId;
    private final LocalDate fechaCompletado;
    private final Long registradoPor;

    public ProgresoDestreza(
            Long id, Long ninoId, Long destrezaId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
            Long registradoPor) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.destrezaId = Objects.requireNonNull(destrezaId, "destrezaId");
        this.anioProgramaObjetivoId = Objects.requireNonNull(anioProgramaObjetivoId, "anioProgramaObjetivoId");
        this.fechaCompletado = requerirNoFutura(fechaCompletado);
        this.registradoPor = Objects.requireNonNull(registradoPor, "registradoPor");
    }

    public static ProgresoDestreza nuevo(
            Long ninoId, Long destrezaId, Long anioProgramaObjetivoId, LocalDate fechaCompletado, Long registradoPor) {
        return new ProgresoDestreza(null, ninoId, destrezaId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaCompletado) {
        Objects.requireNonNull(fechaCompletado, "fechaCompletado");
        if (fechaCompletado.isAfter(LocalDate.now())) {
            throw new ProgresoDestrezaFechaInvalidaException(fechaCompletado);
        }
        return fechaCompletado;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getDestrezaId() {
        return destrezaId;
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
