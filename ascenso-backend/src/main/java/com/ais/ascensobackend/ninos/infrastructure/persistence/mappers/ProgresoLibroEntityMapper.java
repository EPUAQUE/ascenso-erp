package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLibro;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoLibroEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoLibroEntityMapper {

    ProgresoLibro toDomain(ProgresoLibroEntity entity);

    ProgresoLibroEntity toEntity(ProgresoLibro domain);
}
