package com.biblioteca_idat_api.biblioteca_idat_api.mapper;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.AuditoriaDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Prestamo;

public class PrestamoMapper {

    public static PrestamoResponseDTO toDTO(Prestamo p) {
        return new PrestamoResponseDTO(
                p.getId(),
                p.getUsuario().getNombre(),
                p.getLibro().getTitulo(),
                p.getFechaPrestamo(),
                p.getFechaDevolucionEsperada(),
                p.getFechaDevolucionReal(),
                p.getEstado().name(),
                new AuditoriaDTO(
                        p.getCreadoPor(),
                        p.getFechaCreacion(),
                        p.getModificadoPor(),
                        p.getFechaModificacion())
        );
    }
}
