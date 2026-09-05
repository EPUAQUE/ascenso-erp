package com.ais.ascensobackend.ninos.api.dtos.responses;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NinoResponse {

    Long id;
    Long destacamentoId;
    String nombreCompleto;
    LocalDate fechaNacimiento;
    String fotoUrl;
    String encargadoNombre;
    String encargadoContacto;
    String contactoEmergencia;
    LocalDate fechaIngreso;
    Long grupoActualId;
    Long anioProgramaActualId;
    boolean activo;
}
