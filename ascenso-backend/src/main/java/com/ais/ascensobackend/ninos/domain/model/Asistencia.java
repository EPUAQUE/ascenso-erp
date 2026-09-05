package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.AsistenciaFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de asistencia de un {@code Nino} en una fecha concreta, dentro de un
 * {@code Trimestre}. Un niño tiene a lo sumo un registro por fecha
 * (ver {@code ux_asistencia_nino_fecha}).
 */
public class Asistencia {

    private final Long id;
    private final Long ninoId;
    private final Long trimestreId;
    private final LocalDate fecha;
    private boolean presente;
    private final Long registradoPor;

    public Asistencia(
            Long id, Long ninoId, Long trimestreId, LocalDate fecha, boolean presente, Long registradoPor) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.trimestreId = Objects.requireNonNull(trimestreId, "trimestreId");
        this.fecha = requerirNoFutura(fecha);
        this.presente = presente;
        this.registradoPor = Objects.requireNonNull(registradoPor, "registradoPor");
    }

    public static Asistencia nueva(Long ninoId, Long trimestreId, LocalDate fecha, boolean presente, Long registradoPor) {
        return new Asistencia(null, ninoId, trimestreId, fecha, presente, registradoPor);
    }

    public void corregirPresente(boolean presente) {
        this.presente = presente;
    }

    private static LocalDate requerirNoFutura(LocalDate fecha) {
        Objects.requireNonNull(fecha, "fecha");
        if (fecha.isAfter(LocalDate.now())) {
            throw new AsistenciaFechaInvalidaException(fecha);
        }
        return fecha;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getTrimestreId() {
        return trimestreId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }
}
