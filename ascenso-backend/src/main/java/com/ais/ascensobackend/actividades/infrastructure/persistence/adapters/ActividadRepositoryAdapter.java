package com.ais.ascensobackend.actividades.infrastructure.persistence.adapters;

import com.ais.ascensobackend.actividades.domain.model.Actividad;
import com.ais.ascensobackend.actividades.domain.repository.ActividadRepository;
import com.ais.ascensobackend.actividades.infrastructure.persistence.mappers.ActividadEntityMapper;
import com.ais.ascensobackend.actividades.infrastructure.persistence.repositories.ActividadJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ActividadRepositoryAdapter implements ActividadRepository {

    private final ActividadJpaRepository jpaRepository;
    private final ActividadEntityMapper mapper;

    public ActividadRepositoryAdapter(ActividadJpaRepository jpaRepository, ActividadEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Actividad save(Actividad actividad) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(actividad)));
    }

    @Override
    public Optional<Actividad> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Actividad> findByDestacamentoId(Long destacamentoId) {
        return jpaRepository.findByDestacamentoId(destacamentoId).stream().map(mapper::toDomain).toList();
    }
}
