package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.PasoRequeridoRepository;
import com.ais.ascensobackend.ninos.application.dtos.ProgresoRequisitoResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoRequisitoService;
import com.ais.ascensobackend.ninos.domain.exception.ProgresoRequisitoDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.model.ProgresoRequisito;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.ninos.domain.repository.ProgresoRequisitoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoRequisitoServiceImpl implements ProgresoRequisitoService {

    private final ProgresoRequisitoRepository progresoRequisitoRepository;
    private final NinoRepository ninoRepository;
    private final PasoRequeridoRepository pasoRequeridoRepository;

    public ProgresoRequisitoServiceImpl(
            ProgresoRequisitoRepository progresoRequisitoRepository, NinoRepository ninoRepository,
            PasoRequeridoRepository pasoRequeridoRepository) {
        this.progresoRequisitoRepository = progresoRequisitoRepository;
        this.ninoRepository = ninoRepository;
        this.pasoRequeridoRepository = pasoRequeridoRepository;
    }

    @Override
    public List<ProgresoRequisitoResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return progresoRequisitoRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public ProgresoRequisitoResumen registrar(
            Long destacamentoId, Long ninoId, Long pasoRequeridoId, LocalDate fechaCompletado, Long registradoPor) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        pasoRequeridoRepository.findById(pasoRequeridoId)
                .orElseThrow(() -> new ResourceNotFoundException("Paso requerido no encontrado: " + pasoRequeridoId));
        if (progresoRequisitoRepository.existsByNinoIdAndPasoRequeridoId(ninoId, pasoRequeridoId)) {
            throw new ProgresoRequisitoDuplicadoException(ninoId, pasoRequeridoId);
        }
        ProgresoRequisito progreso = ProgresoRequisito.nuevo(ninoId, pasoRequeridoId, fechaCompletado, registradoPor);
        return toResumen(progresoRequisitoRepository.save(progreso));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private ProgresoRequisitoResumen toResumen(ProgresoRequisito progreso) {
        return new ProgresoRequisitoResumen(
                progreso.getId(), progreso.getNinoId(), progreso.getPasoRequeridoId(), progreso.getFechaCompletado(),
                progreso.getRegistradoPor());
    }
}
