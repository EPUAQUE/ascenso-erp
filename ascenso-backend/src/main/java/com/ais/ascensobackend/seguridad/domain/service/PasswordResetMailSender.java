package com.ais.ascensobackend.seguridad.domain.service;

/**
 * Puerto de envío del correo de "olvidé mi contraseña". No debe propagar
 * excepciones: un fallo de envío no debe romper el flujo que lo disparó ni permitir
 * distinguir por código de error si el correo salió o no (ver
 * AuthController.forgotPassword, respuesta siempre genérica).
 */
public interface PasswordResetMailSender {

    void enviar(String correoDestino, String tokenPlano);
}
