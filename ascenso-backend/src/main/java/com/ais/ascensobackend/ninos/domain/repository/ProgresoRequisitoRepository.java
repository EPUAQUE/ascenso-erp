package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.ProgresoRequisito;
import java.util.List;

public interface ProgresoRequisitoRepository {

    ProgresoRequisito save(ProgresoRequisito progresoRequisito);

    List<ProgresoRequisito> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndPasoRequeridoId(Long ninoId, Long pasoRequeridoId);
}
