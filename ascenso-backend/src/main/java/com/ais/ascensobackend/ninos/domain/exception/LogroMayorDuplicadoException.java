package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class LogroMayorDuplicadoException extends BusinessException {

    public LogroMayorDuplicadoException(Long ninoId) {
        super("El niño " + ninoId + " ya recibió el logro mayor.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "LOGRO_MAYOR_DUPLICADO";
    }
}
