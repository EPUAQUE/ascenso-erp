package com.ais.ascensobackend.actividades.infrastructure.persistence.mappers;

import com.ais.ascensobackend.actividades.domain.model.Actividad;
import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.ActividadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActividadEntityMapper {

    Actividad toDomain(ActividadEntity entity);

    ActividadEntity toEntity(Actividad domain);
}
