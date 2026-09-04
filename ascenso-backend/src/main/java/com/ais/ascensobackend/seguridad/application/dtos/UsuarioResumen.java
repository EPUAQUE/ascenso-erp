package com.ais.ascensobackend.seguridad.application.dtos;

import com.ais.ascensobackend.seguridad.domain.model.EstadoUsuario;

public record UsuarioResumen(
        Long id, String username, EstadoUsuario estado, String nombre, String telefono, String correo) {
}
