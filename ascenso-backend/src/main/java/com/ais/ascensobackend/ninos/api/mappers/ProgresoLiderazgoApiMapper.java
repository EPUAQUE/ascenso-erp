package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoLiderazgoResponse;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoLiderazgoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoLiderazgoApiMapper {

    ProgresoLiderazgoResponse toResponse(ProgresoLiderazgoResumen resumen);
}
