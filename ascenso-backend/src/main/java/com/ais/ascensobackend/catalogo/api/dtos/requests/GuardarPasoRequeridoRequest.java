package com.ais.ascensobackend.catalogo.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuardarPasoRequeridoRequest(
        Long anioProgramaId,
        @NotBlank @Size(max = 255) String descripcion,
        @NotNull Short orden) {
}
