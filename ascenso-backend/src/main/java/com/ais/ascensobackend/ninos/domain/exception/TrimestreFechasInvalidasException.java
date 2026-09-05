package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class TrimestreFechasInvalidasException extends BusinessException {

    public TrimestreFechasInvalidasException(LocalDate fechaInicio, LocalDate fechaFin) {
        super("La fecha de fin '" + fechaFin + "' debe ser posterior a la fecha de inicio '" + fechaInicio + "'.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "TRIMESTRE_FECHAS_INVALIDAS";
    }
}
