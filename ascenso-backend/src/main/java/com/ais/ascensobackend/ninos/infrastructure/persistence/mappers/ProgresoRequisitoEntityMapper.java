package com.ais.ascensobackend.ninos.infrastructure.persistence.mappers;

import com.ais.ascensobackend.ninos.domain.model.ProgresoRequisito;
import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoRequisitoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgresoRequisitoEntityMapper {

    ProgresoRequisito toDomain(ProgresoRequisitoEntity entity);

    ProgresoRequisitoEntity toEntity(ProgresoRequisito domain);
}
