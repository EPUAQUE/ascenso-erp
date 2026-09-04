package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.Liderazgo;
import java.util.List;
import java.util.Optional;

public interface LiderazgoRepository {

    Liderazgo save(Liderazgo liderazgo);

    Optional<Liderazgo> findById(Long id);

    List<Liderazgo> findAll();

    List<Liderazgo> findByGrupoId(Long grupoId);
}
