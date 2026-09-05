package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoDestrezaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoDestrezaJpaRepository extends JpaRepository<ProgresoDestrezaEntity, Long> {

    List<ProgresoDestrezaEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndDestrezaId(Long ninoId, Long destrezaId);
}
