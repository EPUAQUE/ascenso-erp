package com.ais.ascensobackend.actividades.application.dtos;

import java.time.Instant;

public record ActividadResumen(
        Long id, Long destacamentoId, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin,
        Long creadoPor) {
}
