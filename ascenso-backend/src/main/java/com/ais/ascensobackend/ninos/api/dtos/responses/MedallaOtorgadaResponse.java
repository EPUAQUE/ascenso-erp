package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MedallaOtorgadaResponse {

    Long id;
    Long ninoId;
    Long anioProgramaId;
    LocalDate fechaOtorgada;
}
