package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.LibroBiblicoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroBiblicoJpaRepository extends JpaRepository<LibroBiblicoEntity, Long> {

    List<LibroBiblicoEntity> findByGrupoId(Long grupoId);
}
