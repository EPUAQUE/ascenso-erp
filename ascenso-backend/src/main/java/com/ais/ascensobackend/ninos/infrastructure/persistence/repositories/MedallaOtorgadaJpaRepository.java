package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.MedallaOtorgadaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedallaOtorgadaJpaRepository extends JpaRepository<MedallaOtorgadaEntity, Long> {

    List<MedallaOtorgadaEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndAnioProgramaId(Long ninoId, Long anioProgramaId);
}
