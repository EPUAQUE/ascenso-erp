package com.ais.ascensobackend.actividades.infrastructure.persistence.repositories;

import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.NinoPadreEntity;
import com.ais.ascensobackend.actividades.infrastructure.persistence.entities.NinoPadreId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NinoPadreJpaRepository extends JpaRepository<NinoPadreEntity, NinoPadreId> {

    List<NinoPadreEntity> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndUsuarioId(Long ninoId, Long usuarioId);

    void deleteByNinoIdAndUsuarioId(Long ninoId, Long usuarioId);
}
