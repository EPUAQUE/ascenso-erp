package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.LogroMayorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogroMayorJpaRepository extends JpaRepository<LogroMayorEntity, Long> {

    Optional<LogroMayorEntity> findByNinoId(Long ninoId);
}
