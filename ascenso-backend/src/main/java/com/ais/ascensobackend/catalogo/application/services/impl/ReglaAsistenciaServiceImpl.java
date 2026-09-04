package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.ReglaAsistenciaResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.ReglaAsistenciaService;
import com.ais.ascensobackend.catalogo.domain.model.ReglaAsistencia;
import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import com.ais.ascensobackend.catalogo.domain.repository.ReglaAsistenciaRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReglaAsistenciaServiceImpl implements ReglaAsistenciaService {

    private final ReglaAsistenciaRepository reglaAsistenciaRepository;

    public ReglaAsistenciaServiceImpl(ReglaAsistenciaRepository reglaAsistenciaRepository) {
        this.reglaAsistenciaRepository = reglaAsistenciaRepository;
    }

    @Override
    public List<ReglaAsistenciaResumen> listar() {
        return reglaAsistenciaRepository.findAll().stream().map(this::toResumen).toList();
    }

    @Override
    public ReglaAsistenciaResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    public ReglaAsistenciaResumen obtenerVigente() {
        return reglaAsistenciaRepository.findVigente()
                .map(this::toResumen)
                .orElseThrow(() -> new ResourceNotFoundException("No hay ninguna regla de asistencia vigente."));
    }

    @Override
    @Transactional
    public ReglaAsistenciaResumen crear(UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
        return toResumen(reglaAsistenciaRepository.save(ReglaAsistencia.nueva(unidad, minimoRequerido, vigenteDesde)));
    }

    @Override
    @Transactional
    public ReglaAsistenciaResumen actualizar(
            Long id, UnidadAsistencia unidad, BigDecimal minimoRequerido, LocalDate vigenteDesde) {
        ReglaAsistencia regla = obtenerORequerido(id);
        regla.actualizarDatos(unidad, minimoRequerido, vigenteDesde);
        return toResumen(reglaAsistenciaRepository.save(regla));
    }

    private ReglaAsistencia obtenerORequerido(Long id) {
        return reglaAsistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de asistencia no encontrada: " + id));
    }

    private ReglaAsistenciaResumen toResumen(ReglaAsistencia regla) {
        return new ReglaAsistenciaResumen(regla.getId(), regla.getUnidad(), regla.getMinimoRequerido(), regla.getVigenteDesde());
    }
}
