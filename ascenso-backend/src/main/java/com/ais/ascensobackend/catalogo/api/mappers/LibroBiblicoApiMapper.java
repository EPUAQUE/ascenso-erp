package com.ais.ascensobackend.catalogo.api.mappers;

import com.ais.ascensobackend.catalogo.api.dtos.responses.LibroBiblicoResponse;
import com.ais.ascensobackend.catalogo.application.dtos.LibroBiblicoResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibroBiblicoApiMapper {

    LibroBiblicoResponse toResponse(LibroBiblicoResumen resumen);
}
