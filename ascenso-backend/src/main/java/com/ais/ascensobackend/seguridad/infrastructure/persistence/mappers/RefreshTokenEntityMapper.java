package com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers;

import com.ais.ascensobackend.seguridad.domain.model.RefreshToken;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityMapper {

    RefreshToken toDomain(RefreshTokenEntity entity);

    RefreshTokenEntity toEntity(RefreshToken domain);
}
