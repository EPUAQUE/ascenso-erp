package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLiderazgo;
import java.util.List;

public interface ProgresoLiderazgoRepository {

    ProgresoLiderazgo save(ProgresoLiderazgo progresoLiderazgo);

    List<ProgresoLiderazgo> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndLiderazgoId(Long ninoId, Long liderazgoId);
}
