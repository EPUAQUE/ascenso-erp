package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.AsistenciaResponse;
import com.ais.ascensobackend.ninos.application.dtos.AsistenciaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AsistenciaApiMapper {

    AsistenciaResponse toResponse(AsistenciaResumen resumen);
}
