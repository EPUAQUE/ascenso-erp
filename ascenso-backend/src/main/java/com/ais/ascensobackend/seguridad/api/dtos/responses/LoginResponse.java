package com.ais.ascensobackend.seguridad.api.dtos.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {

    String accessToken;
    String tokenType;
    long expiresIn;
    boolean debeCambiarPassword;
}
