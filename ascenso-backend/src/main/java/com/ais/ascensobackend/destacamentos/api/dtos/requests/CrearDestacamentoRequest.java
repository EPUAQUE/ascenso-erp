package com.ais.ascensobackend.destacamentos.api.dtos.requests;

import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearDestacamentoRequest(
        @NotBlank(message = "El número único es obligatorio")
        @Size(max = 20, message = "El número único no puede superar 20 caracteres")
        String numeroUnico,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @NotBlank(message = "El nombre de la iglesia es obligatorio")
        @Size(max = 150, message = "El nombre de la iglesia no puede superar 150 caracteres")
        String iglesiaNombre,

        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String direccion,

        @NotNull(message = "El modo de corte de año es obligatorio")
        ModoCorteAnio modoCorteAnio) {
}
