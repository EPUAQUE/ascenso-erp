package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoDestrezaResponse;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoDestrezaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoDestrezaApiMapper {

    ProgresoDestrezaResponse toResponse(ProgresoDestrezaResumen resumen);
}
