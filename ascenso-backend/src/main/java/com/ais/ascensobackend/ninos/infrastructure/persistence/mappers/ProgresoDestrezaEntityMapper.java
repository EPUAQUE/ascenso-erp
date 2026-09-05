package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.ProgresoDestreza;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoDestrezaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoDestrezaEntityMapper {

    ProgresoDestreza toDomain(ProgresoDestrezaEntity entity);

    ProgresoDestrezaEntity toEntity(ProgresoDestreza domain);
}
