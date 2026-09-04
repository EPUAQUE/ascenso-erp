package com.ais.ascensobackend.seguridad.infrastructure.persistence.repositories;

import com.ais.ascensobackend.seguridad.infrastructure.persistence.entities.UsuarioDestacamentoEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDestacamentoJpaRepository extends JpaRepository<UsuarioDestacamentoEntity, Long> {

    List<UsuarioDestacamentoEntity> findByUsuarioId(Long usuarioId);

    List<UsuarioDestacamentoEntity> findByDestacamentoIdIn(Collection<Long> destacamentoIds);
}
