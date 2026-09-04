package com.ais.ascensobackend.catalogo.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DestrezaResponse {

    Long id;
    Long grupoId;
    String nombre;
    String categoria;
    boolean activo;
}
