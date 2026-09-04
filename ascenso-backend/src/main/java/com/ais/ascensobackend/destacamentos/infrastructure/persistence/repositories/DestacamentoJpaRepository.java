package com.ais.ascensobackend.destacamentos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.destacamentos.infrastructure.persistence.entities.DestacamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestacamentoJpaRepository extends JpaRepository<DestacamentoEntity, Long> {

    boolean existsByNumeroUnico(String numeroUnico);
}
