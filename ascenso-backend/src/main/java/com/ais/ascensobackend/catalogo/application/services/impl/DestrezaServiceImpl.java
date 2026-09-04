package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.DestrezaResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.DestrezaService;
import com.ais.ascensobackend.catalogo.domain.model.Destreza;
import com.ais.ascensobackend.catalogo.domain.repository.DestrezaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DestrezaServiceImpl implements DestrezaService {

    private final DestrezaRepository destrezaRepository;
    private final GrupoRepository grupoRepository;

    public DestrezaServiceImpl(DestrezaRepository destrezaRepository, GrupoRepository grupoRepository) {
        this.destrezaRepository = destrezaRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public List<DestrezaResumen> listar(Long grupoId) {
        List<Destreza> destrezas = grupoId == null
                ? destrezaRepository.findAll() : destrezaRepository.findByGrupoId(grupoId);
        return destrezas.stream().map(this::toResumen).toList();
    }

    @Override
    public DestrezaResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public DestrezaResumen crear(Long grupoId, String nombre, String categoria) {
        grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + grupoId));
        return toResumen(destrezaRepository.save(Destreza.nueva(grupoId, nombre, categoria)));
    }

    @Override
    @Transactional
    public DestrezaResumen actualizar(Long id, String nombre, String categoria) {
        Destreza destreza = obtenerORequerido(id);
        destreza.actualizarDatos(nombre, categoria);
        return toResumen(destrezaRepository.save(destreza));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        Destreza destreza = obtenerORequerido(id);
        destreza.activar();
        destrezaRepository.save(destreza);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Destreza destreza = obtenerORequerido(id);
        destreza.desactivar();
        destrezaRepository.save(destreza);
    }

    private Destreza obtenerORequerido(Long id) {
        return destrezaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destreza no encontrada: " + id));
    }

    private DestrezaResumen toResumen(Destreza destreza) {
        return new DestrezaResumen(
                destreza.getId(), destreza.getGrupoId(), destreza.getNombre(), destreza.getCategoria(),
                destreza.isActivo());
    }
}
