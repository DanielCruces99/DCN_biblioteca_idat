package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "PrestamoRequest", description = "Datos requeridos para registrar un préstamo")
public record PrestamoRequestDTO(
        @Schema(description = "Identificador del usuario que recibe el libro", example = "2")
        @NotNull(message = "El usuario es obligatorio")
        @Positive(message = "El usuario debe tener un identificador válido")
        Long usuarioId,

        @Schema(description = "Identificador del libro prestado", example = "1")
        @NotNull(message = "El libro es obligatorio")
        @Positive(message = "El libro debe tener un identificador válido")
        Long libroId,

        @Schema(description = "Cantidad de días del préstamo", example = "14", minimum = "1", maximum = "90")
        @Min(value = 1, message = "El plazo mínimo es de 1 día")
        @Max(value = 90, message = "El plazo máximo es de 90 días")
        int diasPlazo
) {}
