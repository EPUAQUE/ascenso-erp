package com.ais.ascensobackend.seguridad.domain.service;

import java.security.SecureRandom;

/**
 * Genera la contraseña temporal del restablecimiento administrativo. Charset sin
 * caracteres ambiguos (sin {@code 0/O}, {@code 1/l/I}).
 */
public final class TemporaryPasswordGenerator {

    private static final String CHARSET = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int LONGITUD = 20;
    private static final SecureRandom RANDOM = new SecureRandom();

    private TemporaryPasswordGenerator() {
    }

    public static String generar() {
        StringBuilder builder = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            builder.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return builder.toString();
    }
}
