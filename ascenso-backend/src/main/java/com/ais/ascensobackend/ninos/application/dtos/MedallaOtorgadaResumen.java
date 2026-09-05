package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record MedallaOtorgadaResumen(Long id, Long ninoId, Long anioProgramaId, LocalDate fechaOtorgada) {
}
