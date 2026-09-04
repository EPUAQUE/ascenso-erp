package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.GrupoResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.GrupoService;
import com.ais.ascensobackend.catalogo.domain.model.Grupo;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Los 5 grupos etarios son sembrados por Liquibase y fijos en su cantidad — este
 * servicio solo permite editar sus datos descriptivos (nombre, rangos de edad,
 * orden, descripción), no crear ni eliminar grupos vía API.
 */
@Service
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository grupoRepository;

    public GrupoServiceImpl(GrupoRepository grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    @Override
    public List<GrupoResumen> listar() {
        return grupoRepository.findAll().stream().map(this::toResumen).toList();
    }

    @Override
    public GrupoResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public GrupoResumen actualizar(Long id, String nombre, short edadMin, short edadMax, short orden, String descripcion) {
        Grupo grupo = obtenerORequerido(id);
        grupo.actualizarDatos(nombre, edadMin, edadMax, orden, descripcion);
        return toResumen(grupoRepository.save(grupo));
    }

    private Grupo obtenerORequerido(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + id));
    }

    private GrupoResumen toResumen(Grupo grupo) {
        return new GrupoResumen(
                grupo.getId(), grupo.getNombre(), grupo.getEdadMin(), grupo.getEdadMax(), grupo.getOrden(),
                grupo.getDescripcion());
    }
}
