package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.MedallaOtorgadaResponse;
import com.ais.ascensobackend.ninos.application.dtos.MedallaOtorgadaResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedallaOtorgadaApiMapper {

    MedallaOtorgadaResponse toResponse(MedallaOtorgadaResumen resumen);
}
