package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.ninos.application.dtos.TrimestreResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.TrimestreService;
import com.ais.ascensobackend.ninos.domain.exception.TrimestreDuplicadoException;
import com.ais.ascensobackend.ninos.domain.model.Trimestre;
import com.ais.ascensobackend.ninos.domain.repository.TrimestreRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrimestreServiceImpl implements TrimestreService {

    private final TrimestreRepository trimestreRepository;

    public TrimestreServiceImpl(TrimestreRepository trimestreRepository) {
        this.trimestreRepository = trimestreRepository;
    }

    @Override
    public List<TrimestreResumen> listar() {
        return trimestreRepository.findAll().stream().map(this::toResumen).toList();
    }

    @Override
    public TrimestreResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public TrimestreResumen crear(short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
        if (trimestreRepository.existsByAnioCalendarioAndNumero(anioCalendario, numero)) {
            throw new TrimestreDuplicadoException(anioCalendario, numero);
        }
        return toResumen(trimestreRepository.save(Trimestre.nuevo(anioCalendario, numero, fechaInicio, fechaFin)));
    }

    @Override
    @Transactional
    public TrimestreResumen actualizar(
            Long id, short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
        Trimestre trimestre = obtenerORequerido(id);
        boolean claveCambio = trimestre.getAnioCalendario() != anioCalendario || trimestre.getNumero() != numero;
        if (claveCambio && trimestreRepository.existsByAnioCalendarioAndNumero(anioCalendario, numero)) {
            throw new TrimestreDuplicadoException(anioCalendario, numero);
        }
        trimestre.actualizarDatos(anioCalendario, numero, fechaInicio, fechaFin);
        return toResumen(trimestreRepository.save(trimestre));
    }

    private Trimestre obtenerORequerido(Long id) {
        return trimestreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trimestre no encontrado: " + id));
    }

    private TrimestreResumen toResumen(Trimestre trimestre) {
        return new TrimestreResumen(
                trimestre.getId(), trimestre.getAnioCalendario(), trimestre.getNumero(), trimestre.getFechaInicio(),
                trimestre.getFechaFin());
    }
}
