package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.LogroMayorResumen;
import java.time.LocalDate;

public interface LogroMayorService {

    LogroMayorResumen obtener(Long destacamentoId, Long ninoId);

    LogroMayorResumen otorgar(Long destacamentoId, Long ninoId, LocalDate fechaOtorgada);
}
