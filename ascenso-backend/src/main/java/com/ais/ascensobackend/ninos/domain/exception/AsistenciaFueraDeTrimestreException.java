package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class AsistenciaFueraDeTrimestreException extends BusinessException {

    public AsistenciaFueraDeTrimestreException(LocalDate fecha, LocalDate fechaInicio, LocalDate fechaFin) {
        super("La fecha '" + fecha + "' no está dentro del trimestre (" + fechaInicio + " a " + fechaFin + ").");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String errorCode() {
        return "ASISTENCIA_FUERA_DE_TRIMESTRE";
    }
}
