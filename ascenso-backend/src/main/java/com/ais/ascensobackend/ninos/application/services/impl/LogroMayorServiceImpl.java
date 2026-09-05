package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.ninos.application.dtos.LogroMayorResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.LogroMayorService;
import com.ais.ascensobackend.ninos.domain.exception.LogroMayorDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.LogroMayor;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.repository.LogroMayorRepository;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogroMayorServiceImpl implements LogroMayorService {

    private final LogroMayorRepository logroMayorRepository;
    private final NinoRepository ninoRepository;

    public LogroMayorServiceImpl(LogroMayorRepository logroMayorRepository, NinoRepository ninoRepository) {
        this.logroMayorRepository = logroMayorRepository;
        this.ninoRepository = ninoRepository;
    }

    @Override
    public LogroMayorResumen obtener(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return logroMayorRepository.findByNinoId(ninoId)
                .map(this::toResumen)
                .orElseThrow(() -> new ResourceNotFoundException("El niño " + ninoId + " no tiene logro mayor otorgado."));
    }

    @Override
    @Transactional
    public LogroMayorResumen otorgar(Long destacamentoId, Long ninoId, LocalDate fechaOtorgada) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        if (logroMayorRepository.findByNinoId(ninoId).isPresent()) {
            throw new LogroMayorDuplicadoException(ninoId);
        }
        return toResumen(logroMayorRepository.save(LogroMayor.nuevo(ninoId, fechaOtorgada)));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private LogroMayorResumen toResumen(LogroMayor logroMayor) {
        return new LogroMayorResumen(logroMayor.getId(), logroMayor.getNinoId(), logroMayor.getFechaOtorgada());
    }
}
