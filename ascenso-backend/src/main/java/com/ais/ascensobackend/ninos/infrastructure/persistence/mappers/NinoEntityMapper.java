package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.NinoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NinoEntityMapper {

    Nino toDomain(NinoEntity entity);

    NinoEntity toEntity(Nino domain);
}
