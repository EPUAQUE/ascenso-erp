package com.ais.ascensobackend.actividades.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnuncioResponse {

    Long id;
    String titulo;
    String descripcion;
    String imagenUrl;
    String enlaceUrl;
    LocalDate fechaInicioVisible;
    LocalDate fechaFinVisible;
    boolean activo;
    Long creadoPor;
}
