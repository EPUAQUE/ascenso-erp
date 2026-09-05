package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.Nino;
import java.util.List;
import java.util.Optional;

public interface NinoRepository {

    Nino save(Nino nino);

    Optional<Nino> findById(Long id);

    List<Nino> findByDestacamentoId(Long destacamentoId);
}
