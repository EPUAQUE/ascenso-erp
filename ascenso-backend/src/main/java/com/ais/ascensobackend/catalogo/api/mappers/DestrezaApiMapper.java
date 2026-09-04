package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.DestrezaResponse;
import com.ais.ascensobackend.catalogo.application.dtos.DestrezaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestrezaApiMapper {

    DestrezaResponse toResponse(DestrezaResumen resumen);
}
