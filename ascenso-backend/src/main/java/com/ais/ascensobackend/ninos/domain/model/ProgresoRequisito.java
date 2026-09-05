package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.ProgresoRequisitoFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} completó un {@code PasoRequerido} (catálogo).
 * A diferencia de {@link ProgresoLibro}/{@link ProgresoDestreza}/
 * {@link ProgresoLiderazgo}, no lleva año de programa objetivo propio: el paso
 * requerido ya pertenece a un año de programa fijo en el catálogo. Un niño
 * completa cada paso a lo sumo una vez (ver
 * {@code ux_progreso_requisito_nino_paso}). Histórico: no se edita, solo se crea.
 */
public class ProgresoRequisito {

    private final Long id;
    private final Long ninoId;
    private final Long pasoRequeridoId;
    private final LocalDate fechaCompletado;
    private final Long registradoPor;

    public ProgresoRequisito(Long id, Long ninoId, Long pasoRequeridoId, LocalDate fechaCompletado, Long registradoPor) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.pasoRequeridoId = Objects.requireNonNull(pasoRequeridoId, "pasoRequeridoId");
        this.fechaCompletado = requerirNoFutura(fechaCompletado);
        this.registradoPor = Objects.requireNonNull(registradoPor, "registradoPor");
    }

    public static ProgresoRequisito nuevo(
            Long ninoId, Long pasoRequeridoId, LocalDate fechaCompletado, Long registradoPor) {
        return new ProgresoRequisito(null, ninoId, pasoRequeridoId, fechaCompletado, registradoPor);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaCompletado) {
        Objects.requireNonNull(fechaCompletado, "fechaCompletado");
        if (fechaCompletado.isAfter(LocalDate.now())) {
            throw new ProgresoRequisitoFechaInvalidaException(fechaCompletado);
        }
        return fechaCompletado;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getPasoRequeridoId() {
        return pasoRequeridoId;
    }

    public LocalDate getFechaCompletado() {
        return fechaCompletado;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }
}
