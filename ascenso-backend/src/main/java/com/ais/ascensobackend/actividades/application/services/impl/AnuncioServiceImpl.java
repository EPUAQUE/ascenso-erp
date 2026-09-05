package com.ais.ascensobackend.actividades.application.services.impl;

import com.ais.ascensobackend.actividades.application.dtos.AnuncioResumen;
import com.ais.ascensobackend.actividades.application.services.interfaces.AnuncioService;
import com.ais.ascensobackend.actividades.domain.model.Anuncio;
import com.ais.ascensobackend.actividades.domain.repository.AnuncioRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnuncioServiceImpl implements AnuncioService {

    private final AnuncioRepository anuncioRepository;

    public AnuncioServiceImpl(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    @Override
    public List<AnuncioResumen> listar() {
        return anuncioRepository.findAll().stream().map(this::toResumen).toList();
    }

    @Override
    public AnuncioResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public AnuncioResumen crear(
            String titulo, String descripcion, String imagenUrl, String enlaceUrl, LocalDate fechaInicioVisible,
            LocalDate fechaFinVisible, Long creadoPor) {
        Anuncio anuncio = Anuncio.nuevo(
                titulo, descripcion, imagenUrl, enlaceUrl, fechaInicioVisible, fechaFinVisible, creadoPor);
        return toResumen(anuncioRepository.save(anuncio));
    }

    @Override
    @Transactional
    public AnuncioResumen actualizar(
            Long id, String titulo, String descripcion, String imagenUrl, String enlaceUrl,
            LocalDate fechaInicioVisible, LocalDate fechaFinVisible) {
        Anuncio anuncio = obtenerORequerido(id);
        anuncio.actualizarDatos(titulo, descripcion, imagenUrl, enlaceUrl, fechaInicioVisible, fechaFinVisible);
        return toResumen(anuncioRepository.save(anuncio));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        Anuncio anuncio = obtenerORequerido(id);
        anuncio.activar();
        anuncioRepository.save(anuncio);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Anuncio anuncio = obtenerORequerido(id);
        anuncio.desactivar();
        anuncioRepository.save(anuncio);
    }

    private Anuncio obtenerORequerido(Long id) {
        return anuncioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado: " + id));
    }

    private AnuncioResumen toResumen(Anuncio anuncio) {
        return new AnuncioResumen(
                anuncio.getId(), anuncio.getTitulo(), anuncio.getDescripcion(), anuncio.getImagenUrl(),
                anuncio.getEnlaceUrl(), anuncio.getFechaInicioVisible(), anuncio.getFechaFinVisible(),
                anuncio.isActivo(), anuncio.getCreadoPor());
    }
}
