package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoLiderazgoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoLiderazgoJpaRepository extends JpaRepository<ProgresoLiderazgoEntity, Long> {

    List<ProgresoLiderazgoEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndLiderazgoId(Long ninoId, Long liderazgoId);
}
