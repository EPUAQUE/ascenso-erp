package com.ais.ascensobackend.ninos.api.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record PromoverNinoRequest(@NotNull Long grupoId, @NotNull Long anioProgramaId) {
}
