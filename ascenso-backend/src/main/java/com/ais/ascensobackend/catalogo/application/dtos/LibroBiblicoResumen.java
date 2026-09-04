package com.ais.ascensobackend.catalogo.application.dtos;

public record LibroBiblicoResumen(Long id, Long grupoId, String titulo, Short ordenSugerido, boolean activo) {
}
