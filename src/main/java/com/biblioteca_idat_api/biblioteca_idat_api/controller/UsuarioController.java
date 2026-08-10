package com.biblioteca_idat_api.biblioteca_idat_api.controller;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioEstadoDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.service.UsuarioService;
import com.biblioteca_idat_api.biblioteca_idat_api.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuarios", description = "Administración de cuentas y roles")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Registrar un usuario", description = "Cifra la contraseña con BCrypt; requiere rol ADMIN")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Requiere rol ADMIN")
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un usuario", description = "Requiere rol ADMIN")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar un usuario", description = "Requiere rol ADMIN")
    public UsuarioResponseDTO cambiarEstado(@PathVariable Long id,
                                             @Valid @RequestBody UsuarioEstadoDTO request) {
        return usuarioService.cambiarEstado(id, request.activo());
    }
}
