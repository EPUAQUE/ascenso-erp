package com.ais.ascensobackend.destacamentos.api.mappers;

import com.ais.ascensobackend.destacamentos.api.dtos.responses.DestacamentoResponse;
import com.ais.ascensobackend.destacamentos.application.dtos.DestacamentoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestacamentoApiMapper {

    DestacamentoResponse toResponse(DestacamentoResumen resumen);
}
