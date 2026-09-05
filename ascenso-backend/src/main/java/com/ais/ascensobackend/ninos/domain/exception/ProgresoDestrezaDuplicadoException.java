package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgresoDestrezaDuplicadoException extends BusinessException {

    public ProgresoDestrezaDuplicadoException(Long ninoId, Long destrezaId) {
        super("El niño " + ninoId + " ya completó la destreza " + destrezaId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "PROGRESO_DESTREZA_DUPLICADO";
    }
}
