package com.ais.ascensobackend.actividades.infrastructure.persistence.mappers;

import com.ais.ascensobackend.actividades.domain.model.Anuncio;
import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.AnuncioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnuncioEntityMapper {

    Anuncio toDomain(AnuncioEntity entity);

    AnuncioEntity toEntity(Anuncio domain);
}
