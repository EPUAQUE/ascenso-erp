package com.ais.ascensobackend.catalogo.api.dtos.requests;

import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GuardarReglaAsistenciaRequest(
        @NotNull UnidadAsistencia unidad,
        @NotNull BigDecimal minimoRequerido,
        @NotNull LocalDate vigenteDesde) {
}
