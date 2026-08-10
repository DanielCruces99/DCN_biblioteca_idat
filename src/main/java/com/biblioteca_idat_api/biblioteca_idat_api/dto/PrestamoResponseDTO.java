package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(name = "PrestamoResponse", description = "Detalle de un préstamo registrado")
public record PrestamoResponseDTO(
        @Schema(description = "Identificador del préstamo", example = "10")
        Long id,
        @Schema(description = "Nombre del usuario", example = "Juan Pérez")
        String nombreUsuario,
        @Schema(description = "Título del libro", example = "Clean Code")
        String tituloLibro,
        @Schema(description = "Fecha en que se realizó el préstamo", example = "2026-08-09")
        LocalDate fechaPrestamo,
        @Schema(description = "Fecha límite de devolución", example = "2026-08-23")
        LocalDate fechaDevolucionEsperada,
        @Schema(description = "Fecha de devolución; es nula mientras siga activo", example = "2026-08-20", nullable = true)
        LocalDate fechaDevolucionReal,
        @Schema(description = "Estado actual", example = "ACTIVO", allowableValues = {"ACTIVO", "ATRASADO", "DEVUELTO"})
        String estado,
        @Schema(description = "Información generada automáticamente por el sistema", accessMode = Schema.AccessMode.READ_ONLY)
        AuditoriaDTO auditoria
) {}
