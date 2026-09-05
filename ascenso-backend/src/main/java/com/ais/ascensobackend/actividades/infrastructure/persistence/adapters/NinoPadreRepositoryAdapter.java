package com.ais.ascensobackend.actividades.infrastructure.persistence.adapters;

import com.ais.ascensobackend.actividades.domain.model.NinoPadre;
import com.ais.ascensobackend.actividades.domain.repository.NinoPadreRepository;
import com.ais.ascensobackend.actividades.infrastructure.persistence.mappers.NinoPadreEntityMapper;
import com.ais.ascensobackend.actividades.infrastructure.persistence.repositories.NinoPadreJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NinoPadreRepositoryAdapter implements NinoPadreRepository {

    private final NinoPadreJpaRepository jpaRepository;
    private final NinoPadreEntityMapper mapper;

    public NinoPadreRepositoryAdapter(NinoPadreJpaRepository jpaRepository, NinoPadreEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public NinoPadre save(NinoPadre ninoPadre) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(ninoPadre)));
    }

    @Override
    public List<NinoPadre> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndUsuarioId(Long ninoId, Long usuarioId) {
        return jpaRepository.existsByNinoIdAndUsuarioId(ninoId, usuarioId);
    }

    @Override
    public void deleteByNinoIdAndUsuarioId(Long ninoId, Long usuarioId) {
        jpaRepository.deleteByNinoIdAndUsuarioId(ninoId, usuarioId);
    }
}
