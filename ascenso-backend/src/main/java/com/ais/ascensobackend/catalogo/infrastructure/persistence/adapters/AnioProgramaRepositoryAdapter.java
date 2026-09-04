package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.AnioPrograma;
import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.AnioProgramaEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.AnioProgramaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnioProgramaRepositoryAdapter implements AnioProgramaRepository {

    private final AnioProgramaJpaRepository jpaRepository;
    private final AnioProgramaEntityMapper mapper;

    public AnioProgramaRepositoryAdapter(AnioProgramaJpaRepository jpaRepository, AnioProgramaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AnioPrograma save(AnioPrograma anioPrograma) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(anioPrograma)));
    }

    @Override
    public Optional<AnioPrograma> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AnioPrograma> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AnioPrograma> findByGrupoId(Long grupoId) {
        return jpaRepository.findByGrupoId(grupoId).stream().map(mapper::toDomain).toList();
    }
}
