package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarAsistenciaRequest(
        @NotNull Long trimestreId,
        @NotNull LocalDate fecha,
        boolean presente) {
}
