package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.AnioPrograma;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.AnioProgramaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnioProgramaEntityMapper {

    AnioPrograma toDomain(AnioProgramaEntity entity);

    AnioProgramaEntity toEntity(AnioPrograma domain);
}
