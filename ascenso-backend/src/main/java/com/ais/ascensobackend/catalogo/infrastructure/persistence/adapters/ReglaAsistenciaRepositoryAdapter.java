package com.ais.ascensobackend.catalogo.infrastructure.persistence.adapters;

import com.ais.ascensobackend.catalogo.domain.model.ReglaAsistencia;
import com.ais.ascensobackend.catalogo.domain.repository.ReglaAsistenciaRepository;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.mappers.ReglaAsistenciaEntityMapper;
import com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories.ReglaAsistenciaJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReglaAsistenciaRepositoryAdapter implements ReglaAsistenciaRepository {

    private final ReglaAsistenciaJpaRepository jpaRepository;
    private final ReglaAsistenciaEntityMapper mapper;

    public ReglaAsistenciaRepositoryAdapter(
            ReglaAsistenciaJpaRepository jpaRepository, ReglaAsistenciaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ReglaAsistencia save(ReglaAsistencia reglaAsistencia) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(reglaAsistencia)));
    }

    @Override
    public Optional<ReglaAsistencia> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ReglaAsistencia> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ReglaAsistencia> findVigente() {
        return jpaRepository.findVigentesHastaOrdenDesc(LocalDate.now()).stream()
                .findFirst()
                .map(mapper::toDomain);
    }
}
