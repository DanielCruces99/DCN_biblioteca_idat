package com.biblioteca_idat_api.biblioteca_idat_api.controller;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.LibroDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.service.LibroService;
import com.biblioteca_idat_api.biblioteca_idat_api.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@Tag(name = "Libros", description = "Consulta y administración del catálogo")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @Operation(summary = "Listar libros", description = "Admite filtros opcionales por título, autor o disponibilidad")
    public List<LibroDTO> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(defaultValue = "false") boolean disponibles) {
        return libroService.listar(titulo, autor, disponibles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un libro por ID")
    public LibroDTO buscarPorId(@PathVariable Long id) {
        return libroService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Registrar un libro", description = "Requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibroDTO> crear(@Valid @RequestBody LibroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro", description = "Requiere rol ADMIN y conserva la consistencia del stock prestado")
    @PreAuthorize("hasRole('ADMIN')")
    public LibroDTO actualizar(@PathVariable Long id, @Valid @RequestBody LibroDTO dto) {
        return libroService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro", description = "Solo elimina libros sin historial de préstamos; requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
