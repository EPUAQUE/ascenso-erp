package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class LogroMayorFechaInvalidaException extends BusinessException {

    public LogroMayorFechaInvalidaException(LocalDate fechaOtorgada) {
        super("La fecha de otorgamiento '" + fechaOtorgada + "' no puede ser futura.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "LOGRO_MAYOR_FECHA_INVALIDA";
    }
}
