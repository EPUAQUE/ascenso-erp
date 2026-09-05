package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.Asistencia;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.AsistenciaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AsistenciaEntityMapper {

    Asistencia toDomain(AsistenciaEntity entity);

    AsistenciaEntity toEntity(Asistencia domain);
}
