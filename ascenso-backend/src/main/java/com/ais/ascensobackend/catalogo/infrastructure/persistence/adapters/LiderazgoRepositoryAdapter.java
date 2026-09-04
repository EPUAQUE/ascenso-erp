package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.Liderazgo;
import com.ais.ascensobackend.catalogo.domain.repository.LiderazgoRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.LiderazgoEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.LiderazgoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LiderazgoRepositoryAdapter implements LiderazgoRepository {

    private final LiderazgoJpaRepository jpaRepository;
    private final LiderazgoEntityMapper mapper;

    public LiderazgoRepositoryAdapter(LiderazgoJpaRepository jpaRepository, LiderazgoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Liderazgo save(Liderazgo liderazgo) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(liderazgo)));
    }

    @Override
    public Optional<Liderazgo> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Liderazgo> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Liderazgo> findByGrupoId(Long grupoId) {
        return jpaRepository.findByGrupoId(grupoId).stream().map(mapper::toDomain).toList();
    }
}
