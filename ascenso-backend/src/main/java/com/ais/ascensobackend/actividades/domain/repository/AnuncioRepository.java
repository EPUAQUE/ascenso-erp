package com.ais.ascensobackend.actividades.domain.repository;

import com.ais.ascensobackend.actividades.domain.model.Anuncio;
import java.util.List;
import java.util.Optional;

public interface AnuncioRepository {

    Anuncio save(Anuncio anuncio);

    Optional<Anuncio> findById(Long id);

    List<Anuncio> findAll();
}
