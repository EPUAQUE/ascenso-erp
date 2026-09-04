package com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers;

import com.ais.ascensobackend.seguridad.domain.model.Permiso;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.PermisoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermisoEntityMapper {

    Permiso toDomain(PermisoEntity entity);

    PermisoEntity toEntity(Permiso domain);
}
