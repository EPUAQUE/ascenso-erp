package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.ReglaAsistenciaResponse;
import com.ais.ascensobackend.catalogo.application.dtos.ReglaAsistenciaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReglaAsistenciaApiMapper {

    ReglaAsistenciaResponse toResponse(ReglaAsistenciaResumen resumen);
}
