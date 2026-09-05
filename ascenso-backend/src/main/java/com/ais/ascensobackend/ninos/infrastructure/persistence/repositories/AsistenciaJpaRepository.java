package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.AsistenciaEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {

    List<AsistenciaEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndFecha(Long ninoId, LocalDate fecha);
}
