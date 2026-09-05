package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.LibroBiblicoRepository;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoLibroResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoLibroService;
import com.ais.ascensobackend.ninos.domain.exception.ProgresoLibroDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.model.ProgresoLibro;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoLibroRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoLibroServiceImpl implements ProgresoLibroService {

    private final ProgresoLibroRepository progresoLibroRepository;
    private final NinoRepository ninoRepository;
    private final LibroBiblicoRepository libroBiblicoRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public ProgresoLibroServiceImpl(
            ProgresoLibroRepository progresoLibroRepository, NinoRepository ninoRepository,
            LibroBiblicoRepository libroBiblicoRepository, AnioProgramaRepository anioProgramaRepository) {
        this.progresoLibroRepository = progresoLibroRepository;
        this.ninoRepository = ninoRepository;
        this.libroBiblicoRepository = libroBiblicoRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<ProgresoLibroResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return progresoLibroRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public ProgresoLibroResumen registrar(
            Long destacamentoId, Long ninoId, Long libroBiblicoId, Long anioProgramaObjetivoId,
            LocalDate fechaCompletado, Long registradoPor) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        libroBiblicoRepository.findById(libroBiblicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro bíblico no encontrado: " + libroBiblicoId));
        anioProgramaRepository.findById(anioProgramaObjetivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaObjetivoId));
        if (progresoLibroRepository.existsByNinoIdAndLibroBiblicoId(ninoId, libroBiblicoId)) {
            throw new ProgresoLibroDuplicadoException(ninoId, libroBiblicoId);
        }
        ProgresoLibro progreso = ProgresoLibro.nuevo(
                ninoId, libroBiblicoId, anioProgramaObjetivoId, fechaCompletado, registradoPor);
        return toResumen(progresoLibroRepository.save(progreso));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private ProgresoLibroResumen toResumen(ProgresoLibro progreso) {
        return new ProgresoLibroResumen(
                progreso.getId(), progreso.getNinoId(), progreso.getLibroBiblicoId(),
                progreso.getAnioProgramaObjetivoId(), progreso.getFechaCompletado(), progreso.getRegistradoPor());
    }
}
