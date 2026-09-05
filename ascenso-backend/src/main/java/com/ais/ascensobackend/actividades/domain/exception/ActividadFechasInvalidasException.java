package com.ais.ascensobackend.actividades.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class ActividadFechasInvalidasException extends BusinessException {

    public ActividadFechasInvalidasException(Instant fechaInicio, Instant fechaFin) {
        super("La fecha de fin '" + fechaFin + "' debe ser posterior a la fecha de inicio '" + fechaInicio + "'.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "ACTIVIDAD_FECHAS_INVALIDAS";
    }
}
