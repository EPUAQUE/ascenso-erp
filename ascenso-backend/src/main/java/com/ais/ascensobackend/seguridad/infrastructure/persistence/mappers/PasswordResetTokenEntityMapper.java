package com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers;

import com.ais.ascensobackend.seguridad.domain.model.PasswordResetToken;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.PasswordResetTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenEntityMapper {

    PasswordResetToken toDomain(PasswordResetTokenEntity entity);

    PasswordResetTokenEntity toEntity(PasswordResetToken domain);
}
