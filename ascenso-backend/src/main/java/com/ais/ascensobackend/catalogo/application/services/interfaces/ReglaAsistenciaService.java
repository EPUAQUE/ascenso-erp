package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.ReglaAsistenciaResumen;
import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReglaAsistenciaService {

    List<ReglaAsistenciaResumen> listar();

    ReglaAsistenciaResumen obtener(Long id);

    ReglaAsistenciaResumen obtenerVigente();

    ReglaAsistenciaResumen crear(UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde);

    ReglaAsistenciaResumen actualizar(Long id, UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde);
}
