package com.ais.ascensobackend.catalogo.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarGrupoRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotNull Short edadMin,
        @NotNull Short edadMax,
        @NotNull Short orden,
        @Size(max = 255) String descripcion) {
}
