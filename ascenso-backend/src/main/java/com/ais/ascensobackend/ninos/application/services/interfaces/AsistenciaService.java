package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.AsistenciaResumen;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService {

    List<AsistenciaResumen> listar(Long destacamentoId, Long ninoId);

    AsistenciaResumen registrar(
            Long destacamentoId, Long ninoId, Long trimestreId, LocalDate fecha, boolean presente, Long registradoPor);

    AsistenciaResumen corregirPresente(Long destacamentoId, Long ninoId, Long id, boolean presente);
}
