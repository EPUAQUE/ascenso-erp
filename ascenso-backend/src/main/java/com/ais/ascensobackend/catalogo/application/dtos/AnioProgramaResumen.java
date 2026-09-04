package com.ais.ascensobackend.catalogo.application.dtos;

import com.ais.ascensobackend.catalogo.domain.model.Medalla;

public record AnioProgramaResumen(
        Long id, Long grupoId, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
        short minimoLiderazgo, boolean esAnioGracia) {
}
