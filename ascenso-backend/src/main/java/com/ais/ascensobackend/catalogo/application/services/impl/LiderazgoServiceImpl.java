package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.LiderazgoResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.LiderazgoService;
import com.ais.ascensobackend.catalogo.domain.model.Liderazgo;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.catalogo.domain.repository.LiderazgoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiderazgoServiceImpl implements LiderazgoService {

    private final LiderazgoRepository liderazgoRepository;
    private final GrupoRepository grupoRepository;

    public LiderazgoServiceImpl(LiderazgoRepository liderazgoRepository, GrupoRepository grupoRepository) {
        this.liderazgoRepository = liderazgoRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public List<LiderazgoResumen> listar(Long grupoId) {
        List<Liderazgo> liderazgos = grupoId == null
                ? liderazgoRepository.findAll() : liderazgoRepository.findByGrupoId(grupoId);
        return liderazgos.stream().map(this::toResumen).toList();
    }

    @Override
    public LiderazgoResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public LiderazgoResumen crear(Long grupoId, String nombre, String categoria) {
        grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + grupoId));
        return toResumen(liderazgoRepository.save(Liderazgo.nuevo(grupoId, nombre, categoria)));
    }

    @Override
    @Transactional
    public LiderazgoResumen actualizar(Long id, String nombre, String categoria) {
        Liderazgo liderazgo = obtenerORequerido(id);
        liderazgo.actualizarDatos(nombre, categoria);
        return toResumen(liderazgoRepository.save(liderazgo));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        Liderazgo liderazgo = obtenerORequerido(id);
        liderazgo.activar();
        liderazgoRepository.save(liderazgo);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Liderazgo liderazgo = obtenerORequerido(id);
        liderazgo.desactivar();
        liderazgoRepository.save(liderazgo);
    }

    private Liderazgo obtenerORequerido(Long id) {
        return liderazgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liderazgo no encontrado: " + id));
    }

    private LiderazgoResumen toResumen(Liderazgo liderazgo) {
        return new LiderazgoResumen(
                liderazgo.getId(), liderazgo.getGrupoId(), liderazgo.getNombre(), liderazgo.getCategoria(),
                liderazgo.isActivo());
    }
}
