package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.PasoRequerido;
import com.ais.ascensobackend.catalogo.domain.repository.PasoRequeridoRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.PasoRequeridoEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.PasoRequeridoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PasoRequeridoRepositoryAdapter implements PasoRequeridoRepository {

    private final PasoRequeridoJpaRepository jpaRepository;
    private final PasoRequeridoEntityMapper mapper;

    public PasoRequeridoRepositoryAdapter(PasoRequeridoJpaRepository jpaRepository, PasoRequeridoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PasoRequerido save(PasoRequerido pasoRequerido) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(pasoRequerido)));
    }

    @Override
    public Optional<PasoRequerido> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PasoRequerido> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PasoRequerido> findByAnioProgramaId(Long anioProgramaId) {
        return jpaRepository.findByAnioProgramaId(anioProgramaId).stream().map(mapper::toDomain).toList();
    }
}
