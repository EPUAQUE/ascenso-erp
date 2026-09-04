package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.DestrezaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestrezaJpaRepository extends JpaRepository<DestrezaEntity, Long> {

    List<DestrezaEntity> findByGrupoId(Long grupoId);
}
