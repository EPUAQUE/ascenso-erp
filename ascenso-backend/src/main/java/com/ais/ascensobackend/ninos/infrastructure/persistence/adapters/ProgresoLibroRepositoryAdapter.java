package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.ProgresoLibro;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoLibroRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.ProgresoLibroEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.ProgresoLibroJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProgresoLibroRepositoryAdapter implements ProgresoLibroRepository {

    private final ProgresoLibroJpaRepository jpaRepository;
    private final ProgresoLibroEntityMapper mapper;

    public ProgresoLibroRepositoryAdapter(ProgresoLibroJpaRepository jpaRepository, ProgresoLibroEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProgresoLibro save(ProgresoLibro progresoLibro) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(progresoLibro)));
    }

    @Override
    public List<ProgresoLibro> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndLibroBiblicoId(Long ninoId, Long libroBiblicoId) {
        return jpaRepository.existsByNinoIdAndLibroBiblicoId(ninoId, libroBiblicoId);
    }
}
