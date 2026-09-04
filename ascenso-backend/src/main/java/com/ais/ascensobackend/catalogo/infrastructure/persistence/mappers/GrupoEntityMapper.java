package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.Grupo;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.GrupoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrupoEntityMapper {

    Grupo toDomain(GrupoEntity entity);

    GrupoEntity toEntity(Grupo domain);
}
