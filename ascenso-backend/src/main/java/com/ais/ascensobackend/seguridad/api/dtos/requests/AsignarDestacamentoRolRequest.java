package com.ais.ascensobackend.seguridad.api.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record AsignarDestacamentoRolRequest(
        @NotNull(message = "El destacamento es obligatorio") Long destacamentoId,
        @NotNull(message = "El rol es obligatorio") Long rolId) {
}
