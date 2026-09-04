package com.ais.ascensobackend.catalogo.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GrupoResponse {

    Long id;
    String nombre;
    short edadMin;
    short edadMax;
    short orden;
    String descripcion;
}
