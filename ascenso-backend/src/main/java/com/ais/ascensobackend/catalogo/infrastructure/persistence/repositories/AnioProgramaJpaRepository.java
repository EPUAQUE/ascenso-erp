package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.AnioProgramaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnioProgramaJpaRepository extends JpaRepository<AnioProgramaEntity, Long> {

    List<AnioProgramaEntity> findByGrupoId(Long grupoId);
}
