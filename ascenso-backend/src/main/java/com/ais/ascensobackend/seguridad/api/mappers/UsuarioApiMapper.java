package com.ais.ascensobackend.seguridad.api.mappers;

import com.ais.ascensobackend.seguridad.api.dtos.responses.UsuarioNombreResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.UsuarioResponse;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioNombreResumen;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioResumen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioApiMapper {

    UsuarioResponse toResponse(UsuarioResumen resumen);

    UsuarioNombreResponse toResponse(UsuarioNombreResumen resumen);
}
