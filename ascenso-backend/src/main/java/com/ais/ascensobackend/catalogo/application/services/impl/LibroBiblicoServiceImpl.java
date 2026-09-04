package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.LibroBiblicoResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.LibroBiblicoService;
import com.ais.ascensobackend.catalogo.domain.model.LibroBiblico;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.catalogo.domain.repository.LibroBiblicoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroBiblicoServiceImpl implements LibroBiblicoService {

    private final LibroBiblicoRepository libroBiblicoRepository;
    private final GrupoRepository grupoRepository;

    public LibroBiblicoServiceImpl(LibroBiblicoRepository libroBiblicoRepository, GrupoRepository grupoRepository) {
        this.libroBiblicoRepository = libroBiblicoRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public List<LibroBiblicoResumen> listar(Long grupoId) {
        List<LibroBiblico> libros = grupoId == null
                ? libroBiblicoRepository.findAll() : libroBiblicoRepository.findByGrupoId(grupoId);
        return libros.stream().map(this::toResumen).toList();
    }

    @Override
    public LibroBiblicoResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public LibroBiblicoResumen crear(Long grupoId, String titulo, Short ordenSugerido) {
        grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + grupoId));
        return toResumen(libroBiblicoRepository.save(LibroBiblico.nuevo(grupoId, titulo, ordenSugerido)));
    }

    @Override
    @Transactional
    public LibroBiblicoResumen actualizar(Long id, String titulo, Short ordenSugerido) {
        LibroBiblico libro = obtenerORequerido(id);
        libro.actualizarDatos(titulo, ordenSugerido);
        return toResumen(libroBiblicoRepository.save(libro));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        LibroBiblico libro = obtenerORequerido(id);
        libro.activar();
        libroBiblicoRepository.save(libro);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        LibroBiblico libro = obtenerORequerido(id);
        libro.desactivar();
        libroBiblicoRepository.save(libro);
    }

    private LibroBiblico obtenerORequerido(Long id) {
        return libroBiblicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro bíblico no encontrado: " + id));
    }

    private LibroBiblicoResumen toResumen(LibroBiblico libro) {
        return new LibroBiblicoResumen(
                libro.getId(), libro.getGrupoId(), libro.getTitulo(), libro.getOrdenSugerido(), libro.isActivo());
    }
}
