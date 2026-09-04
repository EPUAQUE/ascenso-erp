package com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers;

import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.RolEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PermisoEntityMapper.class)
public interface RolEntityMapper {

    Rol toDomain(RolEntity entity);

    RolEntity toEntity(Rol domain);
}
