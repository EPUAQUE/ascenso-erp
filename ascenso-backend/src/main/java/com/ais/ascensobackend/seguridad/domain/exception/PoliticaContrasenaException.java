package com.ais.ascensobackend.seguridad.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class PoliticaContrasenaException extends BusinessException {

    public PoliticaContrasenaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "PASSWORD_POLICY_VIOLATION";
    }
}
