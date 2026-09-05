package com.ais.ascensobackend.actividades.application.services.interfaces;

import com.ais.ascensobackend.actividades.application.dtos.AnuncioResumen;
import java.time.LocalDate;
import java.util.List;

public interface AnuncioService {

    List<AnuncioResumen> listar();

    AnuncioResumen obtener(Long id);

    AnuncioResumen crear(
            String titulo, String descripcion, String imagenUrl, String enlaceUrl, LocalDate fechaInicioVisible,
            LocalDate fechaFinVisible, Long creadoPor);

    AnuncioResumen actualizar(
            Long id, String titulo, String descripcion, String imagenUrl, String enlaceUrl,
            LocalDate fechaInicioVisible, LocalDate fechaFinVisible);

    void activar(Long id);

    void desactivar(Long id);
}
