package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.ProgresoDestreza;
import java.util.List;

public interface ProgresoDestrezaRepository {

    ProgresoDestreza save(ProgresoDestreza progresoDestreza);

    List<ProgresoDestreza> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndDestrezaId(Long ninoId, Long destrezaId);
}
