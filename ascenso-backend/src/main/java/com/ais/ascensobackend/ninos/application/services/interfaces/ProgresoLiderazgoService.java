package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.ProgresoLiderazgoResumen;
import java.time.LocalDate;
import java.util.List;

public interface ProgresoLiderazgoService {

    List<ProgresoLiderazgoResumen> listar(Long destacamentoId, Long ninoId);

    ProgresoLiderazgoResumen registrar(
            Long destacamentoId, Long ninoId, Long liderazgoId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor);
}
