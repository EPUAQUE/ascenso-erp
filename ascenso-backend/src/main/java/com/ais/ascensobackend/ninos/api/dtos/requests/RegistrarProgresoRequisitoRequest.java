package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarProgresoRequisitoRequest(@NotNull Long pasoRequeridoId, @NotNull LocalDate fechaCompletado) {
}
