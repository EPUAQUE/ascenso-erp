package com.ais.ascensobackend.destacamentos.application.dtos;

import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;

public record DestacamentoResumen(
        Long id, String numeroUnico, String nombre, String iglesiaNombre, String direccion,
        ModoCorteAnio modoCorteAnio, boolean activo) {
}
