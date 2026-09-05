package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarProgresoLibroRequest(
        @NotNull Long libroBiblicoId, @NotNull Long anioProgramaObjetivoId, @NotNull LocalDate fechaCompletado) {
}
