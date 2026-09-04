package com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers;

import com.ais.ascensobackend.seguridad.domain.model.Usuario;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.UsuarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioEntityMapper {

    Usuario toDomain(UsuarioEntity entity);

    UsuarioEntity toEntity(Usuario domain);
}
