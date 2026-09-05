package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgresoDestrezaResponse {

    Long id;
    Long ninoId;
    Long destrezaId;
    Long anioProgramaObjetivoId;
    LocalDate fechaCompletado;
    Long registradoPor;
}
