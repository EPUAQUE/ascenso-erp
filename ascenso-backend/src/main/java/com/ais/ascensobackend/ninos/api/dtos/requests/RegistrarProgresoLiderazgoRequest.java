package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarProgresoLiderazgoRequest(
        @NotNull Long liderazgoId, @NotNull Long anioProgramaObjetivoId, @NotNull LocalDate fechaCompletado) {
}
