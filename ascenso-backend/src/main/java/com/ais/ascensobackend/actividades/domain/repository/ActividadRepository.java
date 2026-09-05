package com.ais.ascensobackend.actividades.domain.repository;

import com.ais.ascensobackend.actividades.domain.model.Actividad;
import java.util.List;
import java.util.Optional;

public interface ActividadRepository {

    Actividad save(Actividad actividad);

    Optional<Actividad> findById(Long id);

    List<Actividad> findByDestacamentoId(Long destacamentoId);
}
