package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.ProgresoLibroEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoLibroJpaRepository extends JpaRepository<ProgresoLibroEntity, Long> {

    List<ProgresoLibroEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndLibroBiblicoId(Long ninoId, Long libroBiblicoId);
}
