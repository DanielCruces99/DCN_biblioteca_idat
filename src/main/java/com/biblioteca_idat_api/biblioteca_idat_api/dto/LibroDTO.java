package com.biblioteca_idat_api.biblioteca_idat_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "Libro", description = "Información de un libro y su disponibilidad en la biblioteca")
public record LibroDTO(
        @Schema(description = "Identificador generado por el sistema", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Título del libro", example = "Clean Code")
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar 200 caracteres")
        String titulo,

        @Schema(description = "Autor del libro", example = "Robert C. Martin")
        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 150, message = "El autor no puede superar 150 caracteres")
        String autor,

        @Schema(description = "ISBN único del libro", example = "9780132350884")
        @NotBlank(message = "El ISBN es obligatorio")
        @Size(max = 20, message = "El ISBN no puede superar 20 caracteres")
        String isbn,

        @Schema(description = "Cantidad total de ejemplares", example = "5", minimum = "0", maximum = "10000")
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        @Max(value = 10000, message = "El stock no puede superar 10000 ejemplares")
        Integer stock,

        @Schema(description = "Ejemplares disponibles para préstamo", example = "4", accessMode = Schema.AccessMode.READ_ONLY)
        Integer stockDisponible,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Schema(description = "Información generada automáticamente por el sistema", accessMode = Schema.AccessMode.READ_ONLY)
        AuditoriaDTO auditoria
) {}
