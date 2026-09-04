package com.ais.ascensobackend.destacamentos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.destacamentos.domain.model.Destacamento;
import com.ais.ascensobackend.destacamentos.infrastructure.persistence.entities.DestacamentoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DestacamentoEntityMapper {

    Destacamento toDomain(DestacamentoEntity entity);

    @Mapping(target = "creadoEn", ignore = true)
    DestacamentoEntity toEntity(Destacamento domain);
}
