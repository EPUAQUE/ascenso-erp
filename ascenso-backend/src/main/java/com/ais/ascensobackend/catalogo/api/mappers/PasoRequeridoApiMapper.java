package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.PasoRequeridoResponse;
import com.ais.ascensobackend.catalogo.application.dtos.PasoRequeridoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasoRequeridoApiMapper {

    PasoRequeridoResponse toResponse(PasoRequeridoResumen resumen);
}
