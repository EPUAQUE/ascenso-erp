package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GuardarTrimestreRequest(
        short anioCalendario,
        @Min(1) @Max(4) short numero,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin) {
}
