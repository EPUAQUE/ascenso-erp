package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AsistenciaResponse {

    Long id;
    Long ninoId;
    Long trimestreId;
    LocalDate fecha;
    boolean presente;
    Long registradoPor;
}
