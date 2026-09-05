package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLibro;
import java.util.List;

public interface ProgresoLibroRepository {

    ProgresoLibro save(ProgresoLibro progresoLibro);

    List<ProgresoLibro> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndLibroBiblicoId(Long ninoId, Long libroBiblicoId);
}
