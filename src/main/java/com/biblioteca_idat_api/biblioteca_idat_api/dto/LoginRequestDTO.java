package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciales requeridas para iniciar sesión")
public record LoginRequestDTO(
        @Schema(description = "Nombre de usuario", example = "admin")
        @NotBlank(message = "El username es obligatorio") String username,

        @Schema(description = "Contraseña del usuario", example = "admin123", format = "password")
        @NotBlank(message = "La contraseña es obligatoria") String password
) {}
