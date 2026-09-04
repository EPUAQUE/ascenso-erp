package com.ais.ascensobackend.catalogo.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LibroBiblicoResponse {

    Long id;
    Long grupoId;
    String titulo;
    Short ordenSugerido;
    boolean activo;
}
