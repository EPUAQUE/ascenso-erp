package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.LibroBiblico;
import java.util.List;
import java.util.Optional;

public interface LibroBiblicoRepository {

    LibroBiblico save(LibroBiblico libroBiblico);

    Optional<LibroBiblico> findById(Long id);

    List<LibroBiblico> findAll();

    List<LibroBiblico> findByGrupoId(Long grupoId);
}
