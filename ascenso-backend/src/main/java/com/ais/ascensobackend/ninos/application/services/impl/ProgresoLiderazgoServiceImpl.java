package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.LiderazgoRepository;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoLiderazgoResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoLiderazgoService;
import com.ais.ascensobackend.ninos.domain.exception.ProgresoLiderazgoDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.model.ProgresoLiderazgo;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoLiderazgoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoLiderazgoServiceImpl implements ProgresoLiderazgoService {

    private final ProgresoLiderazgoRepository progresoLiderazgoRepository;
    private final NinoRepository ninoRepository;
    private final LiderazgoRepository liderazgoRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public ProgresoLiderazgoServiceImpl(
            ProgresoLiderazgoRepository progresoLiderazgoRepository, NinoRepository ninoRepository,
            LiderazgoRepository liderazgoRepository, AnioProgramaRepository anioProgramaRepository) {
        this.progresoLiderazgoRepository = progresoLiderazgoRepository;
        this.ninoRepository = ninoRepository;
        this.liderazgoRepository = liderazgoRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<ProgresoLiderazgoResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return progresoLiderazgoRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public ProgresoLiderazgoResumen registrar(
            Long destacamentoId, Long ninoId, Long liderazgoId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        liderazgoRepository.findById(liderazgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Liderazgo no encontrado: " + liderazgoId));
        anioProgramaRepository.findById(anioProgramaObjetivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaObjetivoId));
        if (progresoLiderazgoRepository.existsByNinoIdAndLiderazgoId(ninoId, liderazgoId)) {
            throw new ProgresoLiderazgoDuplicadoException(ninoId, liderazgoId);
        }
        ProgresoLiderazgo progreso = ProgresoLiderazgo.nuevo(
                ninoId, liderazgoId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
        return toResumen(progresoLiderazgoRepository.save(progreso));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private ProgresoLiderazgoResumen toResumen(ProgresoLiderazgo progreso) {
        return new ProgresoLiderazgoResumen(
                progreso.getId(), progreso.getNinoId(), progreso.getLiderazgoId(),
                progreso.getAnioProgramaObjetivoId(), progreso.getFechaCompletado(), progreso.getRegistradoPor());
    }
}
