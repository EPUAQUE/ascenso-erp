package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.ProgresoDestrezaResumen;
import java.time.LocalDate;
import java.util.List;

public interface ProgresoDestrezaService {

    List<ProgresoDestrezaResumen> listar(Long destacamentoId, Long ninoId);

    ProgresoDestrezaResumen registrar(
            Long destacamentoId, Long ninoId, Long destrezaId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor);
}
