package com.ais.ascensobackend.seguridad.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Única excepción para toda falla al canjear un token de restablecimiento de
 * contraseña (token inexistente, ya usado o expirado). Deliberadamente genérica —
 * igual criterio que {@link AutenticacionFallidaException}.
 */
public class TokenResetInvalidoException extends BusinessException {

    public TokenResetInvalidoException() {
        super("Token inválido o expirado.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "PASSWORD_RESET_TOKEN_INVALID";
    }
}
