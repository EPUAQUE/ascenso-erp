package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ActualizarNinoRequest(
        @NotBlank @Size(max = 150) String nombreCompleto,
        @NotNull LocalDate fechaNacimiento,
        @Size(max = 255) String fotoUrl,
        @NotBlank @Size(max = 150) String encargadoNombre,
        @NotBlank @Size(max = 50) String encargadoContacto,
        @Size(max = 50) String contactoEmergencia) {
}
