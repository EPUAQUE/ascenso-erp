package com.ais.ascensobackend.ninos.domain.repository;

import com.ais.ascensobackend.ninos.domain.model.LogroMayor;
import java.util.Optional;

public interface LogroMayorRepository {

    LogroMayor save(LogroMayor logroMayor);

    Optional<LogroMayor> findByNinoId(Long ninoId);
}
