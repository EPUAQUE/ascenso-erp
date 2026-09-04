package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.LibroBiblico;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.LibroBiblicoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibroBiblicoEntityMapper {

    LibroBiblico toDomain(LibroBiblicoEntity entity);

    LibroBiblicoEntity toEntity(LibroBiblico domain);
}
