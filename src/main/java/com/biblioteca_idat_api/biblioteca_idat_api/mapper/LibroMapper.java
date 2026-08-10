package com.biblioteca_idat_api.biblioteca_idat_api.mapper;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.LibroDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.AuditoriaDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Libro;

public class LibroMapper {

    public static LibroDTO toDTO(Libro l) {
        return new LibroDTO(
                l.getId(),
                l.getTitulo(),
                l.getAutor(),
                l.getIsbn(),
                l.getStock(),
                l.getStockDisponible(),
                new AuditoriaDTO(
                        l.getCreadoPor(),
                        l.getFechaCreacion(),
                        l.getModificadoPor(),
                        l.getFechaModificacion())
        );
    }
}
