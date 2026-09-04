package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.Destreza;
import com.ais.ascensobackend.catalogo.domain.repository.DestrezaRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.DestrezaEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.DestrezaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DestrezaRepositoryAdapter implements DestrezaRepository {

    private final DestrezaJpaRepository jpaRepository;
    private final DestrezaEntityMapper mapper;

    public DestrezaRepositoryAdapter(DestrezaJpaRepository jpaRepository, DestrezaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Destreza save(Destreza destreza) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(destreza)));
    }

    @Override
    public Optional<Destreza> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Destreza> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Destreza> findByGrupoId(Long grupoId) {
        return jpaRepository.findByGrupoId(grupoId).stream().map(mapper::toDomain).toList();
    }
}
