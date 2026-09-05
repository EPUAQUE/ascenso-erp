package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.MedallaOtorgadaResumen;
import java.time.LocalDate;
import java.util.List;

public interface MedallaOtorgadaService {

    List<MedallaOtorgadaResumen> listar(Long destacamentoId, Long ninoId);

    MedallaOtorgadaResumen otorgar(Long destacamentoId, Long ninoId, Long anioProgramaId, LocalDate fechaOtorgada);
}
