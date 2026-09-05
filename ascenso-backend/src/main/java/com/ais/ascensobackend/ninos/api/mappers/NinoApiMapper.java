package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.NinoResponse;
import com.ais.ascensobackend.ninos.application.dtos.NinoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NinoApiMapper {

    NinoResponse toResponse(NinoResumen resumen);
}
