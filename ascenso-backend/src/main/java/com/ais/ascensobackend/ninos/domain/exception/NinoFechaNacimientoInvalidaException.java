package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class NinoFechaNacimientoInvalidaException extends BusinessException {

    public NinoFechaNacimientoInvalidaException(LocalDate fechaNacimiento) {
        super("La fecha de nacimiento '" + fechaNacimiento + "' no puede ser futura.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "NINO_FECHA_NACIMIENTO_INVALIDA";
    }
}
