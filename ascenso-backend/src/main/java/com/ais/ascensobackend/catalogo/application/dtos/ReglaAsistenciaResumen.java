package com.ais.ascensobackend.catalogo.application.dtos;

import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReglaAsistenciaResumen(
        Long id, UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
}
