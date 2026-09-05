package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.LogroMayor;
import com.ais.ascensobackend.ninos.domain.repository.LogroMayorRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.LogroMayorEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.LogroMayorJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LogroMayorRepositoryAdapter implements LogroMayorRepository {

    private final LogroMayorJpaRepository jpaRepository;
    private final LogroMayorEntityMapper mapper;

    public LogroMayorRepositoryAdapter(LogroMayorJpaRepository jpaRepository, LogroMayorEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public LogroMayor save(LogroMayor logroMayor) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(logroMayor)));
    }

    @Override
    public Optional<LogroMayor> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).map(mapper::toDomain);
    }
}
