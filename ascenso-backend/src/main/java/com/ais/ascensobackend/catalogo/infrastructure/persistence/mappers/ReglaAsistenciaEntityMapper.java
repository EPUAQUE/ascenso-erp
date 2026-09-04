package com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers;

import com.ais.ascensobackend.catalogo.domain.model.ReglaAsistencia;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.ReglaAsistenciaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReglaAsistenciaEntityMapper {

    ReglaAsistencia toDomain(ReglaAsistenciaEntity entity);

    ReglaAsistenciaEntity toEntity(ReglaAsistencia domain);
}
