package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.Trimestre;
import com.ais.ascensobackend.ninos.domain.repository.TrimestreRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.TrimestreEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.TrimestreJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TrimestreRepositoryAdapter implements TrimestreRepository {

    private final TrimestreJpaRepository jpaRepository;
    private final TrimestreEntityMapper mapper;

    public TrimestreRepositoryAdapter(TrimestreJpaRepository jpaRepository, TrimestreEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Trimestre save(Trimestre trimestre) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(trimestre)));
    }

    @Override
    public Optional<Trimestre> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Trimestre> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByAnioCalendarioAndNumero(short anioCalendario, short numero) {
        return jpaRepository.existsByAnioCalendarioAndNumero(anioCalendario, numero);
    }
}
