package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.Trimestre;
import java.util.List;
import java.util.Optional;

public interface TrimestreRepository {

    Trimestre save(Trimestre trimestre);

    Optional<Trimestre> findById(Long id);

    List<Trimestre> findAll();

    boolean existsByAnioCalendarioAndNumero(short anioCalendario, short numero);
}
