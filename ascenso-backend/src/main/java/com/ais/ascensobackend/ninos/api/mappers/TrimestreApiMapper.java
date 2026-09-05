package com.ais.ascensobackend.ninos.api.mappers;

import com.ais.ascensobackend.ninos.api.dtos.responses.TrimestreResponse;
import com.ais.ascensobackend.ninos.application.dtos.TrimestreResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrimestreApiMapper {

    TrimestreResponse toResponse(TrimestreResumen resumen);
}
