package com.ais.ascensobackend.seguridad.infrastructure.persistence.adapters;

import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.domain.model.UsuarioDestacamento;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioDestacamentoRepository;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.RolEntity;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.UsuarioDestacamentoEntity;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.mappers.RolEntityMapper;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.repositories.RolJpaRepository;
import com.ais.ascensobackend.seguridad.infrastructure.persistence.repositories.UsuarioDestacamentoJpaRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@code @Transactional} en las lecturas: {@code RolEntity.permisos} es {@code LAZY}
 * y {@code rolMapper.toDomain} lo recorre — necesita una sesión abierta.
 */
@Component
public class UsuarioDestacamentoRepositoryAdapter implements UsuarioDestacamentoRepository {

    private final UsuarioDestacamentoJpaRepository jpaRepository;
    private final RolJpaRepository rolJpaRepository;
    private final RolEntityMapper rolMapper;

    public UsuarioDestacamentoRepositoryAdapter(
            UsuarioDestacamentoJpaRepository jpaRepository, RolJpaRepository rolJpaRepository,
            RolEntityMapper rolMapper) {
        this.jpaRepository = jpaRepository;
        this.rolJpaRepository = rolJpaRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    @Transactional
    public UsuarioDestacamento save(UsuarioDestacamento usuarioDestacamento) {
        RolEntity rolEntity = rolJpaRepository.findById(usuarioDestacamento.getRol().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado: " + usuarioDestacamento.getRol().getId()));
        UsuarioDestacamentoEntity entity = new UsuarioDestacamentoEntity(
                usuarioDestacamento.getId(), usuarioDestacamento.getUsuarioId(),
                usuarioDestacamento.getDestacamentoId(), rolEntity);
        UsuarioDestacamentoEntity guardado = jpaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDestacamento> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Long> listarUsuarioIdsPorDestacamentos(Collection<Long> destacamentoIds) {
        if (destacamentoIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByDestacamentoIdIn(destacamentoIds).stream()
                .map(UsuarioDestacamentoEntity::getUsuarioId)
                .distinct()
                .toList();
    }

    private UsuarioDestacamento toDomain(UsuarioDestacamentoEntity entity) {
        Rol rol = rolMapper.toDomain(entity.getRol());
        return new UsuarioDestacamento(entity.getId(), entity.getUsuarioId(), entity.getDestacamentoId(), rol);
    }
}
