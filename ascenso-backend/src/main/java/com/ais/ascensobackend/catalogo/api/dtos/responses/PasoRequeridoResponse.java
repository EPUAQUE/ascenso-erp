package com.ais.ascensobackend.catalogo.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PasoRequeridoResponse {

    Long id;
    Long anioProgramaId;
    String descripcion;
    short orden;
}
