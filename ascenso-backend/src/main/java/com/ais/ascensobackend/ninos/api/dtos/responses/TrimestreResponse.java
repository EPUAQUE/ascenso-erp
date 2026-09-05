package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrimestreResponse {

    Long id;
    short anioCalendario;
    short numero;
    LocalDate fechaInicio;
    LocalDate fechaFin;
}
