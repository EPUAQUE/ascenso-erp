package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.Grupo;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.GrupoEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.GrupoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GrupoRepositoryAdapter implements GrupoRepository {

    private final GrupoJpaRepository jpaRepository;
    private final GrupoEntityMapper mapper;

    public GrupoRepositoryAdapter(GrupoJpaRepository jpaRepository, GrupoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Grupo save(Grupo grupo) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(grupo)));
    }

    @Override
    public Optional<Grupo> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Grupo> findAll() {
        return jpaRepository.findAll(org.springframework.data.domain.Sort.by("orden")).stream()
                .map(mapper::toDomain).toList();
    }
}
