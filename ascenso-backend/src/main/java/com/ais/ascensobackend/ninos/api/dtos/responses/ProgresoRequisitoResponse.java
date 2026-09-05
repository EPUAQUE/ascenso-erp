package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgresoRequisitoResponse {

    Long id;
    Long ninoId;
    Long pasoRequeridoId;
    LocalDate fechaCompletado;
    Long registradoPor;
}
