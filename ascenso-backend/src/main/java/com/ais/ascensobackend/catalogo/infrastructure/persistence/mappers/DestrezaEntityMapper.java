package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.Destreza;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.DestrezaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestrezaEntityMapper {

    Destreza toDomain(DestrezaEntity entity);

    DestrezaEntity toEntity(Destreza domain);
}
