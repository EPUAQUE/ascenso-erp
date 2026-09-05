package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgresoLiderazgoResponse {

    Long id;
    Long ninoId;
    Long liderazgoId;
    Long anioProgramaObjetivoId;
    LocalDate fechaCompletado;
    Long registradoPor;
}
