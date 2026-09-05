package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgresoLibroResponse {

    Long id;
    Long ninoId;
    Long libroBiblicoId;
    Long anioProgramaObjetivoId;
    LocalDate fechaCompletado;
    Long registradoPor;
}
