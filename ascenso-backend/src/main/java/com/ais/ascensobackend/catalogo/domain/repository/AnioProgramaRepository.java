package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.AnioPrograma;
import java.util.List;
import java.util.Optional;

public interface AnioProgramaRepository {

    AnioPrograma save(AnioPrograma anioPrograma);

    Optional<AnioPrograma> findById(Long id);

    List<AnioPrograma> findAll();

    List<AnioPrograma> findByGrupoId(Long grupoId);
}
