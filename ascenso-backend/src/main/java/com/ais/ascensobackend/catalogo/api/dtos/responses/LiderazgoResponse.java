package com.ais.ascensobackend.catalogo.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LiderazgoResponse {

    Long id;
    Long grupoId;
    String nombre;
    String categoria;
    boolean activo;
}
