package com.ais.ascensobackend.destacamentos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.destacamentos.domain.model.Destacamento;
import com.ais.ascensobackend.destacamentos.domain.repository.DestacamentoRepository;
import com.ais.ascensobackend.destacamentos.infrastructure.persistence.entities.DestacamentoEntity;
import com.ais.ascensobackend.destacamentos.infrastructure.persistence.mappers.DestacamentoEntityMapper;
import com.ais.ascensobackend.destacamentos.infrastructure.persistence.repositories.DestacamentoJpaRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DestacamentoRepositoryAdapter implements DestacamentoRepository {

    private final DestacamentoJpaRepository jpaRepository;
    private final DestacamentoEntityMapper mapper;

    public DestacamentoRepositoryAdapter(DestacamentoJpaRepository jpaRepository, DestacamentoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Destacamento save(Destacamento destacamento) {
        DestacamentoEntity entity = mapper.toEntity(destacamento);
        if (entity.getId() == null) {
            entity.setCreadoEn(Instant.now());
        } else {
            // Conserva creado_en original en actualizaciones — se recupera de la fila
            // existente para no perder el valor real (el mapper lo ignora a propósito).
            jpaRepository.findById(entity.getId()).ifPresent(existente -> entity.setCreadoEn(existente.getCreadoEn()));
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Destacamento> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNumeroUnico(String numeroUnico) {
        return jpaRepository.existsByNumeroUnico(numeroUnico);
    }

    @Override
    public List<Destacamento> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Destacamento> findAllById(Collection<Long> ids) {
        return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).toList();
    }
}
