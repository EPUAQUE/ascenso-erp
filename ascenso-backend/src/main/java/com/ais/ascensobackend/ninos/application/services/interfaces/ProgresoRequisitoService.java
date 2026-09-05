package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.ProgresoRequisitoResumen;
import java.time.LocalDate;
import java.util.List;

public interface ProgresoRequisitoService {

    List<ProgresoRequisitoResumen> listar(Long destacamentoId, Long ninoId);

    ProgresoRequisitoResumen registrar(
            Long destacamentoId, Long ninoId, Long pasoRequeridoId, LocalDate fechaCompletado, Long registradoPor);
}
