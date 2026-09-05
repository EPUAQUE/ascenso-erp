package com.ais.ascensobackend.actividades.api.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record VincularPadreRequest(@NotNull Long usuarioId) {
}
