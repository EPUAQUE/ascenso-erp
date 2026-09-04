package com.ais.ascensobackend.seguridad.api.dtos.responses;

import com.ais.ascensobackend.seguridad.domain.model.EstadoUsuario;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsuarioResponse {

    Long id;
    String username;
    EstadoUsuario estado;
    String nombre;
    String telefono;
    String correo;
}
