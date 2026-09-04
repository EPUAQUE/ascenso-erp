package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.PasoRequeridoResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.PasoRequeridoService;
import com.ais.ascensobackend.catalogo.domain.model.PasoRequerido;
import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.PasoRequeridoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasoRequeridoServiceImpl implements PasoRequeridoService {

    private final PasoRequeridoRepository pasoRequeridoRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public PasoRequeridoServiceImpl(
            PasoRequeridoRepository pasoRequeridoRepository, AnioProgramaRepository anioProgramaRepository) {
        this.pasoRequeridoRepository = pasoRequeridoRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<PasoRequeridoResumen> listar(Long anioProgramaId) {
        List<PasoRequerido> pasos = anioProgramaId == null
                ? pasoRequeridoRepository.findAll() : pasoRequeridoRepository.findByAnioProgramaId(anioProgramaId);
        return pasos.stream().map(this::toResumen).toList();
    }

    @Override
    public PasoRequeridoResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public PasoRequeridoResumen crear(Long anioProgramaId, String descripcion, short orden) {
        anioProgramaRepository.findById(anioProgramaId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaId));
        return toResumen(pasoRequeridoRepository.save(PasoRequerido.nuevo(anioProgramaId, descripcion, orden)));
    }

    @Override
    @Transactional
    public PasoRequeridoResumen actualizar(Long id, String descripcion, short orden) {
        PasoRequerido paso = obtenerORequerido(id);
        paso.actualizarDatos(descripcion, orden);
        return toResumen(pasoRequeridoRepository.save(paso));
    }

    private PasoRequerido obtenerORequerido(Long id) {
        return pasoRequeridoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paso requerido no encontrado: " + id));
    }

    private PasoRequeridoResumen toResumen(PasoRequerido paso) {
        return new PasoRequeridoResumen(paso.getId(), paso.getAnioProgramaId(), paso.getDescripcion(), paso.getOrden());
    }
}
