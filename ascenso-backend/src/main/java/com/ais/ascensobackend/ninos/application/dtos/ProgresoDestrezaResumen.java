package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record ProgresoDestrezaResumen(
        Long id, Long ninoId, Long destrezaId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
        Long registradoPor) {
}
