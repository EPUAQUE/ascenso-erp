package com.ais.ascensobackend.seguridad.api.dtos.responses;

/**
 * Respuesta siempre idéntica, exista o no el usuario, tenga o no correo cargado, y
 * esté o no activo. Nunca debe variar su forma según el caso real, para no
 * habilitar enumeración de usuarios.
 */
public record ForgotPasswordResponse(String mensaje) {
}
