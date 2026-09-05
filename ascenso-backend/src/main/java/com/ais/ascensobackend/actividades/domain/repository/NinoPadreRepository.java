package com.ais.ascensobackend.actividades.domain.repository;

import com.ais.ascensobackend.actividades.domain.model.NinoPadre;
import java.util.List;

public interface NinoPadreRepository {

    NinoPadre save(NinoPadre ninoPadre);

    List<NinoPadre> findByNinoId(Long ninoId);

    boolean existsByNinoIdAndUsuarioId(Long ninoId, Long usuarioId);

    void deleteByNinoIdAndUsuarioId(Long ninoId, Long usuarioId);
}
