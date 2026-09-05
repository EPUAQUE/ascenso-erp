package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record ProgresoLibroResumen(
        Long id, Long ninoId, Long libroBiblicoId, Long anioProgramaObjetivoId, LocalDate fechaCompletado,
        Long registradoPor) {
}
