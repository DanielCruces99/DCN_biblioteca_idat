package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(name = "UsuarioResponse", description = "Información pública de un usuario; nunca expone su contraseña")
public record UsuarioResponseDTO(
        @Schema(description = "Identificador del usuario", example = "2")
        Long id,
        @Schema(description = "Nombre de inicio de sesión", example = "jperez")
        String username,
        @Schema(description = "Nombre completo", example = "Juan Pérez")
        String nombre,
        @Schema(description = "Correo electrónico", example = "jperez@idat.edu.pe")
        String email,
        @Schema(description = "Estado de acceso del usuario", example = "true")
        boolean activo,
        @Schema(description = "Roles asignados", example = "[\"USER\"]")
        Set<String> roles,
        @Schema(description = "Información generada automáticamente por el sistema", accessMode = Schema.AccessMode.READ_ONLY)
        AuditoriaDTO auditoria
) {}
