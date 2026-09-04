package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.PasoRequerido;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.PasoRequeridoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasoRequeridoEntityMapper {

    PasoRequerido toDomain(PasoRequeridoEntity entity);

    PasoRequeridoEntity toEntity(PasoRequerido domain);
}
