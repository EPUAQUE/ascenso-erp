package com.ais.ascensobackend.seguridad.domain.service;

import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.model.Usuario;
import java.time.Instant;

/**
 * Puerto de dominio para la emisión del access token (JWT). La implementación real
 * (firma RS256, claims, TTL) vive en infraestructura; el dominio solo conoce este
 * contrato en lenguaje de negocio.
 */
public interface AccessTokenIssuer {

    Resultado emitir(Usuario usuario, PermisosEfectivos permisos);

    record Resultado(String token, Instant expiraEn) {
    }
}
