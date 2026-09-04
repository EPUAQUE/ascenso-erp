package com.ais.ascensobackend.destacamentos.api.dtos.responses;

import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DestacamentoResponse {

    Long id;
    String numeroUnico;
    String nombre;
    String iglesiaNombre;
    String direccion;
    ModoCorteAnio modoCorteAnio;
    boolean activo;
}
