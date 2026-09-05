package com.ais.ascensobackend.actividades.api.mappers;

import com.ais.ascensobackend.actividades.api.dtos.responses.AnuncioResponse;
import com.ais.ascensobackend.actividades.application.dtos.AnuncioResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnuncioApiMapper {

    AnuncioResponse toResponse(AnuncioResumen resumen);
}
