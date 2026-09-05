package com.ais.ascensobackend.actividades.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NinoPadreResponse {

    Long ninoId;
    Long usuarioId;
}
