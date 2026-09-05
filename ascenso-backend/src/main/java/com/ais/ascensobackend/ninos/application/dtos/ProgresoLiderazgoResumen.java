package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record ProgresoLiderazgoResumen(
        Long id, Long ninoId, Long liderazgoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
        Long registradoPor) {
}
