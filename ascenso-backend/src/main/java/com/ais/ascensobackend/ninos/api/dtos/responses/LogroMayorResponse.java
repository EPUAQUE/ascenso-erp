package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LogroMayorResponse {

    Long id;
    Long ninoId;
    LocalDate fechaOtorgada;
}
