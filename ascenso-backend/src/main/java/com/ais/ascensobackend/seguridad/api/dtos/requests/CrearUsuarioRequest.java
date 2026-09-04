package com.ais.ascensobackend.seguridad.api.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 100, message = "El usuario no puede superar 100 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(max = 256, message = "La contraseña no puede superar 256 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String correo) {
}
