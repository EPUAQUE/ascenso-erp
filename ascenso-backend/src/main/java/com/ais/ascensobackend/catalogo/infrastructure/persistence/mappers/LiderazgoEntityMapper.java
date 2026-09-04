package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.Liderazgo;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.LiderazgoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LiderazgoEntityMapper {

    Liderazgo toDomain(LiderazgoEntity entity);

    LiderazgoEntity toEntity(Liderazgo domain);
}
