package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class AsistenciaDuplicadaException extends BusinessException {

    public AsistenciaDuplicadaException(Long ninoId, LocalDate fecha) {
        super("Ya existe un registro de asistencia para el niño " + ninoId + " en la fecha '" + fecha + "'.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "ASISTENCIA_DUPLICADA";
    }
}
