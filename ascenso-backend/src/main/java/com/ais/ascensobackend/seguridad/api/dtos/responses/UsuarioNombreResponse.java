package com.ais.ascensobackend.seguridad.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsuarioNombreResponse {

    Long id;
    String nombre;
}
