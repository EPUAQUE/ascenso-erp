package com.ais.ascensobackend.ninos.domain.exception;

import com.ais.ascensobackend.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class MedallaOtorgadaDuplicadaException extends BusinessException {

    public MedallaOtorgadaDuplicadaException(Long ninoId, Long anioProgramaId) {
        super("El niño " + ninoId + " ya recibió la medalla del año de programa " + anioProgramaId + ".");
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String errorCode() {
        return "MEDALLA_OTORGADA_DUPLICADA";
    }
}
