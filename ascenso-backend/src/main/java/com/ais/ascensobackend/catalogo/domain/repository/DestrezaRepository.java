package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.Destreza;
import java.util.List;
import java.util.Optional;

public interface DestrezaRepository {

    Destreza save(Destreza destreza);

    Optional<Destreza> findById(Long id);

    List<Destreza> findAll();

    List<Destreza> findByGrupoId(Long grupoId);
}
