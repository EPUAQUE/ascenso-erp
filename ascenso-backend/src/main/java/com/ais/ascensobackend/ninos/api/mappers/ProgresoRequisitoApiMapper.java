package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoRequisitoResponse;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoRequisitoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoRequisitoApiMapper {

    ProgresoRequisitoResponse toResponse(ProgresoRequisitoResumen resumen);
}
