package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.ProgresoDestreza;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoDestrezaRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.ProgresoDestrezaEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.ProgresoDestrezaJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProgresoDestrezaRepositoryAdapter implements ProgresoDestrezaRepository {

    private final ProgresoDestrezaJpaRepository jpaRepository;
    private final ProgresoDestrezaEntityMapper mapper;

    public ProgresoDestrezaRepositoryAdapter(
            ProgresoDestrezaJpaRepository jpaRepository, ProgresoDestrezaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProgresoDestreza save(ProgresoDestreza progresoDestreza) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(progresoDestreza)));
    }

    @Override
    public List<ProgresoDestreza> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndDestrezaId(Long ninoId, Long destrezaId) {
        return jpaRepository.existsByNinoIdAndDestrezaId(ninoId, destrezaId);
    }
}
