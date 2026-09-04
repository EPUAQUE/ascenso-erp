package com.ais.ascensobackend.destacamentos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class DestacamentoDuplicadoException extends BusinessException {

    public DestacamentoDuplicadoException(String numeroUnico) {
        super("Ya existe un destacamento con el número único '" + numeroUnico + "'.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "DESTACAMENTO_DUPLICADO";
    }
}
