package com.ais.ascensobackend.actividades.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record GuardarAnuncioRequest(
        @NotBlank @Size(max = 150) String titulo,
        String descripcion,
        @Size(max = 255) String imagenUrl,
        @Size(max = 255) String enlaceUrl,
        @NotNull LocalDate fechaInicioVisible,
        LocalDate fechaFinVisible) {
}
