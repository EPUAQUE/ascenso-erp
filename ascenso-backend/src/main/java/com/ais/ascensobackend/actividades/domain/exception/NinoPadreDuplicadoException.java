package com.ais.ascensobackend.actividades.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class NinoPadreDuplicadoException extends BusinessException {

    public NinoPadreDuplicadoException(Long ninoId, Long usuarioId) {
        super("El usuario " + usuarioId + " ya está vinculado como padre del niño " + ninoId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "NINO_PADRE_DUPLICADO";
    }
}
