package com.ais.ascensobackend.actividades.api.mappers;

import com.ais.ascensobackend.actividades.api.dtos.responses.NinoPadreResponse;
import com.ais.ascensobackend.actividades.application.dtos.NinoPadreResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NinoPadreApiMapper {

    NinoPadreResponse toResponse(NinoPadreResumen resumen);
}
