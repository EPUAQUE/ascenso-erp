package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.AnioProgramaResponse;
import com.ais.ascensobackend.catalogo.application.dtos.AnioProgramaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnioProgramaApiMapper {

    AnioProgramaResponse toResponse(AnioProgramaResumen resumen);
}
