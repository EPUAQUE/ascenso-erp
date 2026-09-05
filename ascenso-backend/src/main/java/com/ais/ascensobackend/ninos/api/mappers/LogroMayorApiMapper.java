package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.LogroMayorResponse;
import com.ais.ascensobackend.ninos.application.dtos.LogroMayorResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogroMayorApiMapper {

    LogroMayorResponse toResponse(LogroMayorResumen resumen);
}
