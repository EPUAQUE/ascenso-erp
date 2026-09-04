package com.ais.ascensobackend.catalogo.application.dtos;

public record GrupoResumen(Long id, String nombre, short edadMin, short edadMax, short orden, String descripcion) {
}
