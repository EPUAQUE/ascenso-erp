package com.ais.ascensobackend.seguridad.domain.repository;

import com.ais.ascensobackend.seguridad.domain.model.PasswordResetToken;
import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /** Marca como usados todos los tokens sin usar de este usuario — invalida cualquier solicitud previa. */
    void invalidarNoUsadosDeUsuario(Long usuarioId);

    /**
     * Consume el token atómicamente: {@code UPDATE ... SET usado = true WHERE
     * token_hash = :tokenHash AND usado = false AND expira_en > :ahora}. Devuelve 1 si
     * esta llamada fue la que lo consumió, 0 si ya estaba usado o expirado.
     */
    int consumir(String tokenHash, Instant ahora);
}
