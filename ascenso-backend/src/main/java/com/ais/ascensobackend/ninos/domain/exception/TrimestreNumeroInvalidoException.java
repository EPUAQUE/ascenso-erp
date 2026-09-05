package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class TrimestreNumeroInvalidoException extends BusinessException {

    public TrimestreNumeroInvalidoException(short numero) {
        super("El número de trimestre '" + numero + "' debe estar entre 1 y 4.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "TRIMESTRE_NUMERO_INVALIDO";
    }
}
