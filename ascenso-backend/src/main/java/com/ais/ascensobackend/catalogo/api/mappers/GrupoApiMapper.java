package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.GrupoResponse;
import com.ais.ascensobackend.catalogo.application.dtos.GrupoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrupoApiMapper {

    GrupoResponse toResponse(GrupoResumen resumen);
}
