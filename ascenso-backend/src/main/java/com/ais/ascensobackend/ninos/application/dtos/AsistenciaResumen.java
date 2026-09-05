package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record AsistenciaResumen(
        Long id, Long ninoId, Long trimestreId, LocalDate fecha, boolean presente, Long registradoPor) {
}
