package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.MedallaOtorgada;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.MedallaOtorgadaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedallaOtorgadaEntityMapper {

    MedallaOtorgada toDomain(MedallaOtorgadaEntity entity);

    MedallaOtorgadaEntity toEntity(MedallaOtorgada domain);
}
