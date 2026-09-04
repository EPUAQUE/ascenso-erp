package com.ais.ascensobackend.destacamentos.domain.repository;

import com.ais.ascensobackend.destacamentos.domain.model.Destacamento;
import java.util.List;
import java.util.Optional;

public interface DestacamentoRepository {

    Destacamento save(Destacamento destacamento);

    Optional<Destacamento> findById(Long id);

    boolean existsByNumeroUnico(String numeroUnico);

    List<Destacamento> findAll();
}
