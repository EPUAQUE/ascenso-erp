package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.ProgresoRequisito;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoRequisitoRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.ProgresoRequisitoEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.ProgresoRequisitoJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProgresoRequisitoRepositoryAdapter implements ProgresoRequisitoRepository {

    private final ProgresoRequisitoJpaRepository jpaRepository;
    private final ProgresoRequisitoEntityMapper mapper;

    public ProgresoRequisitoRepositoryAdapter(
            ProgresoRequisitoJpaRepository jpaRepository, ProgresoRequisitoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProgresoRequisito save(ProgresoRequisito progresoRequisito) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(progresoRequisito)));
    }

    @Override
    public List<ProgresoRequisito> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndPasoRequeridoId(Long ninoId, Long pasoRequeridoId) {
        return jpaRepository.existsByNinoIdAndPasoRequeridoId(ninoId, pasoRequeridoId);
    }
}
