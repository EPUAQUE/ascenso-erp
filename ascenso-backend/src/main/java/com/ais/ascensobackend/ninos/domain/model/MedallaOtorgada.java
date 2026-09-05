package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.MedallaOtorgadaFechaInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Registro de que un {@code Nino} recibió la medalla de un año de programa
 * (ver {@code AnioPrograma.medalla} en catálogo). Un niño recibe la medalla de
 * cada año de programa a lo sumo una vez (ver
 * {@code ux_medalla_otorgada_nino_anio}). Histórico: no se edita, solo se crea.
 */
public class MedallaOtorgada {

    private final Long id;
    private final Long ninoId;
    private final Long anioProgramaId;
    private final LocalDate fechaOtorgada;

    public MedallaOtorgada(Long id, Long ninoId, Long anioProgramaId, LocalDate fechaOtorgada) {
        this.id = id;
        this.ninoId = Objects.requireNonNull(ninoId, "ninoId");
        this.anioProgramaId = Objects.requireNonNull(anioProgramaId, "anioProgramaId");
        this.fechaOtorgada = requerirNoFutura(fechaOtorgada);
    }

    public static MedallaOtorgada nueva(Long ninoId, Long anioProgramaId, LocalDate fechaOtorgada) {
        return new MedallaOtorgada(null, ninoId, anioProgramaId, fechaOtorgada);
    }

    private static LocalDate requerirNoFutura(LocalDate fechaOtorgada) {
        Objects.requireNonNull(fechaOtorgada, "fechaOtorgada");
        if (fechaOtorgada.isAfter(LocalDate.now())) {
            throw new MedallaOtorgadaFechaInvalidaException(fechaOtorgada);
        }
        return fechaOtorgada;
    }

    public Long getId() {
        return id;
    }

    public Long getNinoId() {
        return ninoId;
    }

    public Long getAnioProgramaId() {
        return anioProgramaId;
    }

    public LocalDate getFechaOtorgada() {
        return fechaOtorgada;
    }
}
