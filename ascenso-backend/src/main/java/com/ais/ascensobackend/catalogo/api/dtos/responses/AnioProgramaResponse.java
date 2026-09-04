package com.ais.ascensobackend.catalogo.api.dtos.responses;

import com.ais.ascensobackend.catalogo.domain.model.Medalla;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnioProgramaResponse {

    Long id;
    Long grupoId;
    short numero;
    Medalla medalla;
    short minimoLibros;
    short minimoDestrezas;
    short minimoLiderazgo;
    boolean esAnioGracia;
}
