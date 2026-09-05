package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record ProgresoRequisitoResumen(
        Long id, Long ninoId, Long pasoRequeridoId, LocalDate fechaCompletado, Long registradoPor) {
}
