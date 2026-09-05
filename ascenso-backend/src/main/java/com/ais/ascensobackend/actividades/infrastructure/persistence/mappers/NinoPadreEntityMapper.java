package com.ais.ascensobackend.actividades.infrastructure.persistence.mappers;

import com.ais.ascensobackend.actividades.domain.model.NinoPadre;
import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.NinoPadreEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NinoPadreEntityMapper {

    NinoPadre toDomain(NinoPadreEntity entity);

    NinoPadreEntity toEntity(NinoPadre domain);
}
