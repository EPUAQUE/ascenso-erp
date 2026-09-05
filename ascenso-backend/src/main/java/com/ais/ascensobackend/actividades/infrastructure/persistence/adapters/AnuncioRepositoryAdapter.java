package com.ais.ascensobackend.actividades.infrastructure.persistence.adapters;

import com.ais.ascensobackend.actividades.domain.model.Anuncio;
import com.ais.ascensobackend.actividades.domain.repository.AnuncioRepository;
import com.ais.ascensobackend.actividades.infrastructure.persistence.mappers.AnuncioEntityMapper;
import com.ais.ascensobackend.actividades.infrastructure.persistence.repositories.AnuncioJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnuncioRepositoryAdapter implements AnuncioRepository {

    private final AnuncioJpaRepository jpaRepository;
    private final AnuncioEntityMapper mapper;

    public AnuncioRepositoryAdapter(AnuncioJpaRepository jpaRepository, AnuncioEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Anuncio save(Anuncio anuncio) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(anuncio)));
    }

    @Override
    public Optional<Anuncio> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Anuncio> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
