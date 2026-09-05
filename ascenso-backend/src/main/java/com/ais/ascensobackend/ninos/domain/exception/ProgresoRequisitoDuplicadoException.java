package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgresoRequisitoDuplicadoException extends BusinessException {

    public ProgresoRequisitoDuplicadoException(Long ninoId, Long pasoRequeridoId) {
        super("El niño " + ninoId + " ya completó el paso requerido " + pasoRequeridoId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "PROGRESO_REQUISITO_DUPLICADO";
    }
}
