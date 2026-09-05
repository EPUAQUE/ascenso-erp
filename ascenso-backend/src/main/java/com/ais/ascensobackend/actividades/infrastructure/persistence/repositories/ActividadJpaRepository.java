package com.ais.ascensobackend.actividades.infrastructure.persistence.repositories;

import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.ActividadEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadJpaRepository extends JpaRepository<ActividadEntity, Long> {

    List<ActividadEntity> findByDestacamentoId(Long destacamentoId);
}
