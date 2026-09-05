package com.ais.ascensobackend.actividades.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class AnuncioFechasInvalidasException extends BusinessException {

    public AnuncioFechasInvalidasException(LocalDate fechaInicioVisible, LocalDate fechaFinVisible) {
        super("La fecha de fin visible '" + fechaFinVisible + "' debe ser posterior a la fecha de inicio visible '"
                + fechaInicioVisible + "'.");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "ANUNCIO_FECHAS_INVALIDAS";
    }
}
