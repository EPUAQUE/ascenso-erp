package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarProgresoDestrezaRequest(
        @NotNull Long destrezaId, @NotNull Long anioProgramaObjetivoId, @NotNull LocalDate fechaCompletado) {
}
