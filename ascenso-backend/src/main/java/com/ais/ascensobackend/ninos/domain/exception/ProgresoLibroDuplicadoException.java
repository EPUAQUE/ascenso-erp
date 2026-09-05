package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgresoLibroDuplicadoException extends BusinessException {

    public ProgresoLibroDuplicadoException(Long ninoId, Long libroBiblicoId) {
        super("El niño " + ninoId + " ya completó el libro bíblico " + libroBiblicoId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "PROGRESO_LIBRO_DUPLICADO";
    }
}
