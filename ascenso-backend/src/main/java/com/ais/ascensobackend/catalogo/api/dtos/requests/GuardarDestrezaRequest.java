package com.ais.ascensobackend.catalogo.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuardarDestrezaRequest(
        Long grupoId,
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 50) String categoria) {
}
