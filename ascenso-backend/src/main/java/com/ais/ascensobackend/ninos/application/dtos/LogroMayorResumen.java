package com.ais.ascensobackend.ninos.application.dtos;

import java.time.LocalDate;

public record LogroMayorResumen(Long id, Long ninoId, LocalDate fechaOtorgada) {
}
