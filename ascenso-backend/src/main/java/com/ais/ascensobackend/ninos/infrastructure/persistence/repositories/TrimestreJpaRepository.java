package com.ais.ascensobackend.ninos.infrastructure.persistence.repositories;

import com.ais.ascensobackend.ninos.infrastructure.persistence.entities.TrimestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrimestreJpaRepository extends JpaRepository<TrimestreEntity, Long> {

    boolean existsByAnioCalendarioAndNumero(short anioCalendario, short numero);
}
