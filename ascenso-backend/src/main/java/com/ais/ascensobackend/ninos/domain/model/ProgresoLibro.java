package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.ProgresoLibroFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} completó un {@code LibroBiblico} (catálogo)
 * rumbo a un año de programa objetivo. Un niño completa cada libro a lo sumo
 * una vez (ver {@code ux_progreso_libro_nino_libro}). Histórico: no se edita,
 * solo se crea.
 */
public class ProgresoLibro {

    private final Long id;
    private final Long ninoId;
    private final Long libroBiblicoId;
    private final Long anioProgramaObjetivoId;
    private final LocalDate fechaCompletado;
    private final Long registradoPor;

    public ProgresoLibro(
            Long id, Long ninoId, Long libroBiblicoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
            Long registradoPor) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.libroBiblicoId = Objects.requireNonNull(libroBiblicoId, "libroBiblicoId");
        this.anioProgramaObjetivoId = Objects.requireNonNull(anioProgramaObjetivoId, "anioProgramaObjetivoId");
        this.fechaCompletado = requerirNoFutura(fechaCompletado);
        this.registradoPor = Objects.requireNonNull(registradoPor, "registradoPor");
    }

    public static ProgresoLibro nuevo(
            Long ninoId, Long libroBiblicoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
            Long registradoPor) {
        return new ProgresoLibro(null, ninoId, libroBiblicoId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaCompletado) {
        Objects.requireNonNull(fechaCompletado, "fechaCompletado");
        if (fechaCompletado.isAfter(LocalDate.now())) {
            throw new ProgresoLibroFechaInvalidaException(fechaCompletado);
        }
        return fechaCompletado;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getLibroBiblicoId() {
        return libroBiblicoId;
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
