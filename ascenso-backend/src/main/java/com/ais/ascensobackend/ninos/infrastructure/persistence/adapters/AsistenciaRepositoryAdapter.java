package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.Asistencia;
import com.ais.ascensobackend.ninos.domain.repository.AsistenciaRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.AsistenciaEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.AsistenciaJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AsistenciaRepositoryAdapter implements AsistenciaRepository {

    private final AsistenciaJpaRepository jpaRepository;
    private final AsistenciaEntityMapper mapper;

    public AsistenciaRepositoryAdapter(AsistenciaJpaRepository jpaRepository, AsistenciaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Asistencia save(Asistencia asistencia) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(asistencia)));
    }

    @Override
    public Optional<Asistencia> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Asistencia> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndFecha(Long ninoId, LocalDate fecha) {
        return jpaRepository.existsByNinoIdAndFecha(ninoId, fecha);
    }
}
