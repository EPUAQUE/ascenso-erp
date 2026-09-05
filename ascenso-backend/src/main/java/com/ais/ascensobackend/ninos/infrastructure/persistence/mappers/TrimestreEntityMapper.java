package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.Trimestre;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.TrimestreEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrimestreEntityMapper {

    Trimestre toDomain(TrimestreEntity entity);

    TrimestreEntity toEntity(Trimestre domain);
}
