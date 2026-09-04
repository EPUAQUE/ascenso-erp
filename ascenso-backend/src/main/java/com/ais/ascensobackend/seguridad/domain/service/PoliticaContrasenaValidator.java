package com.ais.ascensobackend.seguridad.domain.service;

import com.ais.ascensobackend.seguridad.domain.exception.PoliticaContrasenaException;

/**
 * Política orientada a longitud (frases largas permitidas, sin reglas de
 * composición). Longitud medida en code points, no en unidades UTF-16.
 */
public final class PoliticaContrasenaValidator {

    private PoliticaContrasenaValidator() {
    }

    public static void validar(String passwordPlano, int minLength, int maxLength) {
        if (passwordPlano == null || passwordPlano.isEmpty()) {
            throw new PoliticaContrasenaException("La contraseña es obligatoria.");
        }
        int longitud = passwordPlano.codePointCount(0, passwordPlano.length());
        if (longitud < minLength) {
            throw new PoliticaContrasenaException("La contraseña debe tener al menos " + minLength + " caracteres.");
        }
        if (longitud > maxLength) {
            throw new PoliticaContrasenaException("La contraseña no puede superar " + maxLength + " caracteres.");
        }
    }
}
