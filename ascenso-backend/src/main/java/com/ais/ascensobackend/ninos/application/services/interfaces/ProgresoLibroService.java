package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.ProgresoLibroResumen;
import java.time.LocalDate;
import java.util.List;

public interface ProgresoLibroService {

    List<ProgresoLibroResumen> listar(Long destacamentoId, Long ninoId);

    ProgresoLibroResumen registrar(
            Long destacamentoId, Long ninoId, Long libroBiblicoId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor);
}
