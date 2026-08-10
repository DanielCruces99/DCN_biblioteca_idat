package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "Tokens generados después de una autenticación correcta")
public record LoginResponseDTO(
        @Schema(description = "JWT de acceso para autorizar solicitudes", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "Token utilizado para renovar la sesión", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken,

        @Schema(description = "Esquema de autenticación", example = "Bearer")
        String tokenType
) {}
