package com.ais.ascensobackend.catalogo.api.dtos.requests;

import com.ais.ascensobackend.catalogo.domain.model.Medalla;
import jakarta.validation.constraints.NotNull;

public record GuardarAnioProgramaRequest(
        Long grupoId,
        @NotNull Short numero,
        Medalla medalla,
        @NotNull Short minimoLibros,
        @NotNull Short minimoDestrezas,
        @NotNull Short minimoLiderazgo,
        @NotNull Boolean esAnioGracia) {
}
