package com.ais.ascensobackend.seguridad.application.dtos;

public record LoginResult(String accessToken, String refreshToken, long expiresInSeconds, boolean debeCambiarPassword) {
}
