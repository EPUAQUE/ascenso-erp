package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.Asistencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository {

    Asistencia save(Asistencia asistencia);

    Optional<Asistencia> findById(Long id);

    List<Asistencia> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndFecha(Long ninoId, LocalDate fecha);
}
