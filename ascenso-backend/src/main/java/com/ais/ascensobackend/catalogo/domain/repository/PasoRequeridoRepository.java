package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.PasoRequerido;
import java.util.List;
import java.util.Optional;

public interface PasoRequeridoRepository {

    PasoRequerido save(PasoRequerido pasoRequerido);

    Optional<PasoRequerido> findById(Long id);

    List<PasoRequerido> findAll();

    List<PasoRequerido> findByAnioProgramaId(Long anioProgramaId);
}
