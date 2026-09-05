package com.ais.ascensobackend.actividades.api.dtos.responses;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActividadResponse {

    Long id;
    Long destacamentoId;
    String titulo;
    String descripcion;
    Instant fechaInicio;
    Instant fechaFin;
    Long creadoPor;
}
