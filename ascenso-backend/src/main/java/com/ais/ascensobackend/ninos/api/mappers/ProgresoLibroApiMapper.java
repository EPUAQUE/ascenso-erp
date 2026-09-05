package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoLibroResponse;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoLibroResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoLibroApiMapper {

    ProgresoLibroResponse toResponse(ProgresoLibroResumen resumen);
}
