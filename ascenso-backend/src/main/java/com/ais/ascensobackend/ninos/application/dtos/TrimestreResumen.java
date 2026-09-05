package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record TrimestreResumen(Long id, short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
}
