package com.ais.ascensobackend.actividades.infrastructure.persistence.repositories;

import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.AnuncioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnuncioJpaRepository extends JpaRepository<AnuncioEntity, Long> {
}
