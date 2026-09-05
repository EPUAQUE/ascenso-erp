package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.ninos.application.dtos.AsistenciaResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.AsistenciaService;
import com.ais.ascensobackend.ninos.domain.exception.AsistenciaDuplicadaException;
import com.ais.ascensobackend.ninos.domain.exception.AsistenciaFueraDeTrimestreException;
import com.ais.ascensobackend.ninos.domain.model.Asistencia;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.model.Trimestre;
import com.ais.ascensobackend.ninos.domain.repository.AsistenciaRepository;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.domain.repository.TrimestreRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final NinoRepository ninoRepository;
    private final TrimestreRepository trimestreRepository;

    public AsistenciaServiceImpl(
            AsistenciaRepository asistenciaRepository, NinoRepository ninoRepository,
            TrimestreRepository trimestreRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.ninoRepository = ninoRepository;
        this.trimestreRepository = trimestreRepository;
    }

    @Override
    public List<AsistenciaResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return asistenciaRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public AsistenciaResumen registrar(
            Long destacamentoId, Long ninoId, Long trimestreId, LocalDate fecha, boolean presente, Long registradoPor) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        Trimestre trimestre = trimestreRepository.findById(trimestreId)
                .orElseThrow(() -> new ResourceNotFoundException("Trimestre no encontrado: " + trimestreId));
        if (fecha.isBefore(trimestre.getFechaInicio()) || fecha.isAfter(trimestre.getFechaFin())) {
            throw new AsistenciaFueraDeTrimestreException(fecha, trimestre.getFechaInicio(), trimestre.getFechaFin());
        }
        if (asistenciaRepository.existsByNinoIdAndFecha(ninoId, fecha)) {
            throw new AsistenciaDuplicadaException(ninoId, fecha);
        }
        Asistencia asistencia = Asistencia.nueva(ninoId, trimestreId, fecha, presente, registradoPor);
        return toResumen(asistenciaRepository.save(asistencia));
    }

    @Override
    @Transactional
    public AsistenciaResumen corregirPresente(Long destacamentoId, Long ninoId, Long id, boolean presente) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada: " + id));
        if (!asistencia.getNinoId().equals(ninoId)) {
            throw new ResourceNotFoundException("Asistencia no encontrada: " + id);
        }
        asistencia.corregirPresente(presente);
        return toResumen(asistenciaRepository.save(asistencia));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private AsistenciaResumen toResumen(Asistencia asistencia) {
        return new AsistenciaResumen(
                asistencia.getId(), asistencia.getNinoId(), asistencia.getTrimestreId(), asistencia.getFecha(),
                asistencia.isPresente(), asistencia.getRegistradoPor());
    }
}
