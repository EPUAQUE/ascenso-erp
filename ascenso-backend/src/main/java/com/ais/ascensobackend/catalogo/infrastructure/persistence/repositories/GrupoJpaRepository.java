package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.GrupoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoJpaRepository extends JpaRepository<GrupoEntity, Long> {
}
