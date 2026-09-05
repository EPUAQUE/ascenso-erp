package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.LogroMayor;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.LogroMayorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogroMayorEntityMapper {

    LogroMayor toDomain(LogroMayorEntity entity);

    LogroMayorEntity toEntity(LogroMayor domain);
}
