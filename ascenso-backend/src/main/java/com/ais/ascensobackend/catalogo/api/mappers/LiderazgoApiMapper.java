package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.LiderazgoResponse;
import com.ais.ascensobackend.catalogo.application.dtos.LiderazgoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LiderazgoApiMapper {

    LiderazgoResponse toResponse(LiderazgoResumen resumen);
}
