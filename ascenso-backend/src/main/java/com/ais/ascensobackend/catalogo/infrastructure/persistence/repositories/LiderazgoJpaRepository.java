package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.LiderazgoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiderazgoJpaRepository extends JpaRepository<LiderazgoEntity, Long> {

    List<LiderazgoEntity> findByGrupoId(Long grupoId);
}
