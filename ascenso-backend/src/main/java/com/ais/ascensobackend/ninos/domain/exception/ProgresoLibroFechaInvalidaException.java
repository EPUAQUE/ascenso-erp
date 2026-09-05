package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class ProgresoLibroFechaInvalidaException extends BusinessException {

    public ProgresoLibroFechaInvalidaException(LocalDate fechaCompletado) {
        super("La fecha de completado '" + fechaCompletado + "' no puede ser futura.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "PROGRESO_LIBRO_FECHA_INVALIDA";
    }
}
