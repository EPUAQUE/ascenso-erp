package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class AsistenciaFechaInvalidaException extends BusinessException {

    public AsistenciaFechaInvalidaException(LocalDate fecha) {
        super("La fecha de asistencia '" + fecha + "' no puede ser futura.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "ASISTENCIA_FECHA_INVALIDA";
    }
}
