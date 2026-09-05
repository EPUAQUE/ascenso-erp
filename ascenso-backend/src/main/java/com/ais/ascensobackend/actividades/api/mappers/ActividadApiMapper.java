package com.ais.ascensobackend.actividades.api.mappers;

import com.ais.ascensobackend.actividades.api.dtos.responses.ActividadResponse;
import com.ais.ascensobackend.actividades.application.dtos.ActividadResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActividadApiMapper {

    ActividadResponse toResponse(ActividadResumen resumen);
}
