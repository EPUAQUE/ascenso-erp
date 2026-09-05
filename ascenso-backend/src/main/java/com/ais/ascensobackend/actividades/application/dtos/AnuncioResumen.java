package com.ais.ascensobackend.actividades.application.dtos;

import java.time.LocalDate;

public record AnuncioResumen(
        Long id, String titulo, String descripcion, String imagenUrl, String enlaceUrl,
        LocalDate fechaInicioVisible, LocalDate fechaFinVisible, boolean activo, Long creadoPor) {
}
