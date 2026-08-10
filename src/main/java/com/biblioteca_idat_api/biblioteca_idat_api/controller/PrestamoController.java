package com.biblioteca_idat_api.biblioteca_idat_api.controller;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.security.UserPrincipal;
import com.biblioteca_idat_api.biblioteca_idat_api.service.PrestamoService;
import com.biblioteca_idat_api.biblioteca_idat_api.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@Tag(name = "Préstamos", description = "Registro, devolución e historial de préstamos")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @Operation(summary = "Registrar un préstamo", description = "Requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrestamoResponseDTO> crear(@Valid @RequestBody PrestamoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crearPrestamo(request));
    }

    @PatchMapping("/{id}/devolucion")
    @Operation(summary = "Registrar una devolución", description = "Requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrestamoResponseDTO> registrarDevolucion(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id));
    }

    @GetMapping("/mios")
    @Operation(summary = "Consultar mi historial", description = "Devuelve únicamente los préstamos del usuario autenticado")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<PrestamoResponseDTO> misPrestamos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return prestamoService.listarPorUsuario(userPrincipal.getId());
    }
}
