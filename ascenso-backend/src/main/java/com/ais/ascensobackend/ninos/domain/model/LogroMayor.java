package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.LogroMayorFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} alcanzó el logro mayor del programa — el
 * reconocimiento máximo, otorgado a lo sumo una vez por niño (ver
 * {@code ux_logro_mayor_nino}). Histórico: no se edita, solo se crea.
 */
public class LogroMayor {

    private final Long id;
    private final Long ninoId;
    private final LocalDate fechaOtorgada;

    public LogroMayor(Long id, Long ninoId, LocalDate fechaOtorgada) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.fechaOtorgada = requerirNoFutura(fechaOtorgada);
    }

    public static LogroMayor nuevo(Long ninoId, LocalDate fechaOtorgada) {
        return new LogroMayor(null, ninoId, fechaOtorgada);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaOtorgada) {
        Objects.requireNonNull(fechaOtorgada, "fechaOtorgada");
        if (fechaOtorgada.isAfter(LocalDate.now())) {
            throw new LogroMayorFechaInvalidaException(fechaOtorgada);
        }
        return fechaOtorgada;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public LocalDate getFechaOtorgada() {
        return fechaOtorgada;
    }
}
