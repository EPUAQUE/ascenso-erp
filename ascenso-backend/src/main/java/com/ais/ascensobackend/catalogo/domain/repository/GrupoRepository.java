package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.Grupo;
import java.util.List;
import java.util.Optional;

public interface GrupoRepository {

    Grupo save(Grupo grupo);

    Optional<Grupo> findById(Long id);

    List<Grupo> findAll();
}
