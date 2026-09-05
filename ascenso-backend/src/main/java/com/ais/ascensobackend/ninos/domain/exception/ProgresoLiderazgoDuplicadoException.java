package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgresoLiderazgoDuplicadoException extends BusinessException {

    public ProgresoLiderazgoDuplicadoException(Long ninoId, Long liderazgoId) {
        super("El niño " + ninoId + " ya completó el liderazgo " + liderazgoId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "PROGRESO_LIDERAZGO_DUPLICADO";
    }
}
