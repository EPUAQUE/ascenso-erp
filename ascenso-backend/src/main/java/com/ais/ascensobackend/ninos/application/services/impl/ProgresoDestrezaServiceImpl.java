package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.DestrezaRepository;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoDestrezaResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoDestrezaService;
import com.ais.ascensobackend.ninos.domain.exception.ProgresoDestrezaDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.model.ProgresoDestreza;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoDestrezaRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoDestrezaServiceImpl implements ProgresoDestrezaService {

    private final ProgresoDestrezaRepository progresoDestrezaRepository;
    private final NinoRepository ninoRepository;
    private final DestrezaRepository destrezaRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public ProgresoDestrezaServiceImpl(
            ProgresoDestrezaRepository progresoDestrezaRepository, NinoRepository ninoRepository,
            DestrezaRepository destrezaRepository, AnioProgramaRepository anioProgramaRepository) {
        this.progresoDestrezaRepository = progresoDestrezaRepository;
        this.ninoRepository = ninoRepository;
        this.destrezaRepository = destrezaRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<ProgresoDestrezaResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return progresoDestrezaRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public ProgresoDestrezaResumen registrar(
            Long destacamentoId, Long ninoId, Long destrezaId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        destrezaRepository.findById(destrezaId)
                .orElseThrow(() -> new ResourceNotFoundException("Destreza no encontrada: " + destrezaId));
        anioProgramaRepository.findById(anioProgramaObjetivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaObjetivoId));
        if (progresoDestrezaRepository.existsByNinoIdAndDestrezaId(ninoId, destrezaId)) {
            throw new ProgresoDestrezaDuplicadoException(ninoId, destrezaId);
        }
        ProgresoDestreza progreso = ProgresoDestreza.nuevo(
                ninoId, destrezaId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
        return toResumen(progresoDestrezaRepository.save(progreso));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private ProgresoDestrezaResumen toResumen(ProgresoDestreza progreso) {
        return new ProgresoDestrezaResumen(
                progreso.getId(), progreso.getNinoId(), progreso.getDestrezaId(),
                progreso.getAnioProgramaObjetivoId(), progreso.getFechaCompletado(), progreso.getRegistradoPor());
    }
}
