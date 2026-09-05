package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class MedallaOtorgadaFechaInvalidaException extends BusinessException {

    public MedallaOtorgadaFechaInvalidaException(LocalDate fechaOtorgada) {
        super("La fecha de otorgamiento '" + fechaOtorgada + "' no puede ser futura.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "MEDALLA_OTORGADA_FECHA_INVALIDA";
    }
}
