package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLiderazgo;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoLiderazgoRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.ProgresoLiderazgoEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.ProgresoLiderazgoJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProgresoLiderazgoRepositoryAdapter implements ProgresoLiderazgoRepository {

    private final ProgresoLiderazgoJpaRepository jpaRepository;
    private final ProgresoLiderazgoEntityMapper mapper;

    public ProgresoLiderazgoRepositoryAdapter(
            ProgresoLiderazgoJpaRepository jpaRepository, ProgresoLiderazgoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProgresoLiderazgo save(ProgresoLiderazgo progresoLiderazgo) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(progresoLiderazgo)));
    }

    @Override
    public List<ProgresoLiderazgo> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndLiderazgoId(Long ninoId, Long liderazgoId) {
        return jpaRepository.existsByNinoIdAndLiderazgoId(ninoId, liderazgoId);
    }
}
