package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UsuarioEstado", description = "Nuevo estado de habilitación de un usuario")
public record UsuarioEstadoDTO(
        @Schema(description = "Indica si el usuario puede acceder al sistema", example = "true")
        @NotNull(message = "El estado activo es obligatorio") Boolean activo
) {}
