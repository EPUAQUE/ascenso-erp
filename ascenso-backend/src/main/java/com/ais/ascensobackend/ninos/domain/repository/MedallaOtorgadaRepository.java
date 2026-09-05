package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.MedallaOtorgada;
import java.util.List;

public interface MedallaOtorgadaRepository {

    MedallaOtorgada save(MedallaOtorgada medallaOtorgada);

    List<MedallaOtorgada> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndAnioProgramaId(Long ninoId, Long anioProgramaId);
}
