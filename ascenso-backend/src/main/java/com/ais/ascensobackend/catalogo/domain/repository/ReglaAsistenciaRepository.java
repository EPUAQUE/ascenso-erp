package com.ais.ascensobackend.catalogo.domain.repository;

import com.ais.ascensobackend.catalogo.domain.model.ReglaAsistencia;
import java.util.List;
import java.util.Optional;

public interface ReglaAsistenciaRepository {

    ReglaAsistencia save(ReglaAsistencia reglaAsistencia);

    Optional<ReglaAsistencia> findById(Long id);

    List<ReglaAsistencia> findAll();

    /** La regla vigente hoy: la de {@code vigenteDesde} más reciente que ya inició. */
    Optional<ReglaAsistencia> findVigente();
}
