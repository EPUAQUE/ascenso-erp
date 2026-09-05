package com.ais.ascensobackend.actividades.application.services.interfaces;

import com.ais.ascensobackend.actividades.application.dtos.ActividadResumen;
import java.time.Instant;
import java.util.List;

public interface ActividadService {

    List<ActividadResumen> listar(Long destacamentoId);

    ActividadResumen obtener(Long destacamentoId, Long id);

    ActividadResumen crear(
            Long destacamentoId, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin,
            Long creadoPor);

    ActividadResumen actualizar(
            Long destacamentoId, Long id, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin);
}
