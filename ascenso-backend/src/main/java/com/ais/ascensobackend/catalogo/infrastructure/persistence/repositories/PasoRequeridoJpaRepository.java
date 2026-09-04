package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.PasoRequeridoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasoRequeridoJpaRepository extends JpaRepository<PasoRequeridoEntity, Long> {

    List<PasoRequeridoEntity> findByAnioProgramaId(Long anioProgramaId);
}
