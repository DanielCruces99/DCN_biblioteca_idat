package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RefreshRequest", description = "Token requerido para renovar o cerrar una sesión")
public record RefreshRequestDTO(
        @Schema(description = "Refresh token vigente", example = "eyJhbGciOiJIUzI1NiJ9...")
        @NotBlank(message = "El refresh token es obligatorio") String refreshToken
) {}
