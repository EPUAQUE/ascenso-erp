package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoRequisitoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoRequisitoJpaRepository extends JpaRepository<ProgresoRequisitoEntity, Long> {

    List<ProgresoRequisitoEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndPasoRequeridoId(Long ninoId, Long pasoRequeridoId);
}
