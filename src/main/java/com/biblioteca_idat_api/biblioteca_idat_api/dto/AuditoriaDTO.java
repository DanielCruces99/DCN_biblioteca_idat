package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "Auditoria", description = "Trazabilidad de creación y última modificación del registro")
public record AuditoriaDTO(
        @Schema(description = "Usuario que creó el registro", example = "admin")
        String creadoPor,

        @Schema(description = "Fecha y hora de creación", example = "2026-08-09T19:30:00")
        LocalDateTime fechaCreacion,

        @Schema(description = "Usuario que realizó la última modificación", example = "admin")
        String modificadoPor,

        @Schema(description = "Fecha y hora de la última modificación", example = "2026-08-09T19:45:12")
        LocalDateTime fechaModificacion
) {}
