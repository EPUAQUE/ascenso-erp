package com.ais.ascensobackend.actividades.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record GuardarActividadRequest(
        @NotBlank @Size(max = 150) String titulo,
        String descripcion,
        @NotNull Instant fechaInicio,
        Instant fechaFin) {
}
