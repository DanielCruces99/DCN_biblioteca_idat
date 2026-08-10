package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(name = "UsuarioRequest", description = "Datos requeridos para registrar un usuario")
public record UsuarioRequestDTO(
        @Schema(description = "Nombre único utilizado para iniciar sesión", example = "jperez")
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username contiene caracteres no permitidos")
        String username,

        @Schema(description = "Nombre completo", example = "Juan Pérez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @Schema(description = "Correo electrónico único", example = "jperez@idat.edu.pe")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @Schema(description = "Contraseña de 8 a 72 caracteres", example = "Usuario123", format = "password")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @Schema(description = "Roles que se asignarán al usuario", example = "[\"USER\"]", allowableValues = {"ADMIN", "USER"})
        Set<String> roles
) {}
