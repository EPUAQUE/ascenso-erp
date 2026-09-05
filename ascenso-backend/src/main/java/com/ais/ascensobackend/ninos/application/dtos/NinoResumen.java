package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record NinoResumen(
        Long id,
        Long destacamentoId,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        String fotoUrl,
        String encargadoNombre,
        String encargadoContacto,
        String contactoEmergencia,
        LocalDate fechaIngreso,
        Long grupoActualId,
        Long anioProgramaActualId,
        boolean activo) {
}
