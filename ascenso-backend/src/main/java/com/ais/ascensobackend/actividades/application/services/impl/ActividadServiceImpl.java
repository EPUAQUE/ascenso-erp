package com.ais.ascensobackend.actividades.application.services.impl;

import com.ais.ascensobackend.actividades.application.dtos.ActividadResumen;
import com.ais.ascensobackend.actividades.application.services.interfaces.ActividadService;
import com.ais.ascensobackend.actividades.domain.model.Actividad;
import com.ais.ascensobackend.actividades.domain.repository.ActividadRepository;
import com.ais.ascensobackend.destacamentos.domain.repository.DestacamentoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;
    private final DestacamentoRepository destacamentoRepository;

    public ActividadServiceImpl(ActividadRepository actividadRepository, DestacamentoRepository destacamentoRepository) {
        this.actividadRepository = actividadRepository;
        this.destacamentoRepository = destacamentoRepository;
    }

    @Override
    public List<ActividadResumen> listar(Long destacamentoId) {
        return actividadRepository.findByDestacamentoId(destacamentoId).stream().map(this::toResumen).toList();
    }

    @Override
    public ActividadResumen obtener(Long destacamentoId, Long id) {
        return toResumen(obtenerORequerida(destacamentoId, id));
    }

    @Override
    @Transactional
    public ActividadResumen crear(
            Long destacamentoId, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin,
            Long creadoPor) {
        destacamentoRepository.findById(destacamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Destacamento no encontrado: " + destacamentoId));
        Actividad actividad = Actividad.nueva(destacamentoId, titulo, descripcion, fechaInicio, fechaFin, creadoPor);
        return toResumen(actividadRepository.save(actividad));
    }

    @Override
    @Transactional
    public ActividadResumen actualizar(
            Long destacamentoId, Long id, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin) {
        Actividad actividad = obtenerORequerida(destacamentoId, id);
        actividad.actualizarDatos(titulo, descripcion, fechaInicio, fechaFin);
        return toResumen(actividadRepository.save(actividad));
    }

    private Actividad obtenerORequerida(Long destacamentoId, Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada: " + id));
        if (!actividad.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Actividad no encontrada: " + id);
        }
        return actividad;
    }

    private ActividadResumen toResumen(Actividad actividad) {
        return new ActividadResumen(
                actividad.getId(), actividad.getDestacamentoId(), actividad.getTitulo(), actividad.getDescripcion(),
                actividad.getFechaInicio(), actividad.getFechaFin(), actividad.getCreadoPor());
    }
}
