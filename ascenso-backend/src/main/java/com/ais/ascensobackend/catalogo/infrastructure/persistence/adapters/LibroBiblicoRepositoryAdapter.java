package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.LibroBiblico;
import com.ais.ascensobackend.catalogo.domain.repository.LibroBiblicoRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.LibroBiblicoEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.LibroBiblicoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LibroBiblicoRepositoryAdapter implements LibroBiblicoRepository {

    private final LibroBiblicoJpaRepository jpaRepository;
    private final LibroBiblicoEntityMapper mapper;

    public LibroBiblicoRepositoryAdapter(LibroBiblicoJpaRepository jpaRepository, LibroBiblicoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public LibroBiblico save(LibroBiblico libroBiblico) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(libroBiblico)));
    }

    @Override
    public Optional<LibroBiblico> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<LibroBiblico> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<LibroBiblico> findByGrupoId(Long grupoId) {
        return jpaRepository.findByGrupoId(grupoId).stream().map(mapper::toDomain).toList();
    }
}
