package com.ais.ascensobackend.catalogo.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuardarLibroBiblicoRequest(
        Long grupoId,
        @NotBlank @Size(max = 120) String titulo,
        Short ordenSugerido) {
}
