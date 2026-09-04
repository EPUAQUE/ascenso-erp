package com.ais.ascensobackend.seguridad.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

/**
 * {@code passwordTemporal} solo existe en esta respuesta — nunca se persiste en
 * texto plano ni se registra en auditoría.
 */
@Value
@Builder
public class RestablecerPasswordResponse {

    String passwordTemporal;
}
