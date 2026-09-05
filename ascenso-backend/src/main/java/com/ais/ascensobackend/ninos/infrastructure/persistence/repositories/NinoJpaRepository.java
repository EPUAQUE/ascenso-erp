package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.NinoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NinoJpaRepository extends JpaRepository<NinoEntity, Long> {

    List<NinoEntity> findByDestacamentoId(Long destacamentoId);
}
