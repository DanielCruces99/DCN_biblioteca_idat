package com.biblioteca_idat_api.biblioteca_idat_api.mapper;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.AuditoriaDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;

import java.util.stream.Collectors;

public class UsuarioMapper {

    public static UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getNombre(),
                u.getEmail(),
                u.isActivo(),
                u.getRoles().stream()
                        .map(rol -> rol.getNombre())
                        .collect(Collectors.toSet()),
                new AuditoriaDTO(
                        u.getCreadoPor(),
                        u.getFechaCreacion(),
                        u.getModificadoPor(),
                        u.getFechaModificacion())
        );
    }

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.username());
        u.setNombre(dto.nombre());
        u.setEmail(dto.email());
        u.setPassword(dto.password());
        return u;
    }
}
