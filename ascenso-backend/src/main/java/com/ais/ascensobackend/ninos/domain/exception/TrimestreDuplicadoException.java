package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class TrimestreDuplicadoException extends BusinessException {

    public TrimestreDuplicadoException(short anioCalendario, short numero) {
        super("Ya existe el trimestre " + numero + " del año " + anioCalendario + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "TRIMESTRE_DUPLICADO";
    }
}
