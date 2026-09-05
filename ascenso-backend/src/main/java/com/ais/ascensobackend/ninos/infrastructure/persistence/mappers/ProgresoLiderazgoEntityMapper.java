package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLiderazgo;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoLiderazgoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoLiderazgoEntityMapper {

    ProgresoLiderazgo toDomain(ProgresoLiderazgoEntity entity);

    ProgresoLiderazgoEntity toEntity(ProgresoLiderazgo domain);
}
