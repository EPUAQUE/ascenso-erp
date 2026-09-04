package com.ais.ascensobackend.catalogo.api.dtos.responses;

import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReglaAsistenciaResponse {

    Long id;
    UnidadAsistencia unidad;
    BigDecimal minimoRequerido;
    LocalDate vigenteDesde;
}
