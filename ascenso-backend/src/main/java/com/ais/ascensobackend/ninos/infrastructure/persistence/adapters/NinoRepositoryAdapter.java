package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.NinoEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.NinoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class NinoRepositoryAdapter implements NinoRepository {

    private final NinoJpaRepository jpaRepository;
    private final NinoEntityMapper mapper;

    public NinoRepositoryAdapter(NinoJpaRepository jpaRepository, NinoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Nino save(Nino nino) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(nino)));
    }

    @Override
    public Optional<Nino> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Nino> findByDestacamentoId(Long destacamentoId) {
        return jpaRepository.findByDestacamentoId(destacamentoId).stream().map(mapper::toDomain).toList();
    }
}
