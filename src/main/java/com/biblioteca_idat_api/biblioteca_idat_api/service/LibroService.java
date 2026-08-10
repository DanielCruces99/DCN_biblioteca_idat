package com.biblioteca_idat_api.biblioteca_idat_api.service;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.LibroDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Libro;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.RecursoDuplicadoException;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.RecursoNoEncontradoException;
import com.biblioteca_idat_api.biblioteca_idat_api.mapper.LibroMapper;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.LibroRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.PrestamoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public LibroService(LibroRepository libroRepository, PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public List<LibroDTO> listar(String titulo, String autor, boolean soloDisponibles) {
        List<Libro> libros;
        if (titulo != null && !titulo.isBlank()) {
            libros = libroRepository.findByTituloContainingIgnoreCase(titulo.trim());
        } else if (autor != null && !autor.isBlank()) {
            libros = libroRepository.findByAutorContainingIgnoreCase(autor.trim());
        } else if (soloDisponibles) {
            libros = libroRepository.findDisponibles();
        } else {
            libros = libroRepository.findAll();
        }
        return libros.stream().map(LibroMapper::toDTO).toList();
    }

    public LibroDTO buscarPorId(Long id) {
        return LibroMapper.toDTO(obtenerLibro(id));
    }

    @Transactional
    public LibroDTO crear(LibroDTO request) {
        String isbn = request.isbn().trim();
        if (libroRepository.existsByIsbn(isbn)) {
            throw new RecursoDuplicadoException("Ya existe un libro con el ISBN indicado");
        }

        Libro libro = new Libro();
        copiarDatosEditables(libro, request);
        libro.setStockDisponible(request.stock());
        return LibroMapper.toDTO(libroRepository.save(libro));
    }

    @Transactional
    public LibroDTO actualizar(Long id, LibroDTO request) {
        Libro libro = obtenerLibro(id);
        libroRepository.findByIsbn(request.isbn().trim())
                .filter(encontrado -> !encontrado.getId().equals(id))
                .ifPresent(encontrado -> {
                    throw new RecursoDuplicadoException("Ya existe otro libro con el ISBN indicado");
                });

        int ejemplaresPrestados = libro.getStock() - libro.getStockDisponible();
        if (request.stock() < ejemplaresPrestados) {
            throw new IllegalStateException(
                    "El stock total no puede ser menor que los ejemplares actualmente prestados: "
                            + ejemplaresPrestados);
        }

        copiarDatosEditables(libro, request);
        libro.setStockDisponible(request.stock() - ejemplaresPrestados);
        return LibroMapper.toDTO(libroRepository.save(libro));
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = obtenerLibro(id);
        if (prestamoRepository.countByLibroId(id) > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar un libro que forma parte del historial de préstamos");
        }
        libroRepository.delete(libro);
    }

    private Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado: " + id));
    }

    private void copiarDatosEditables(Libro libro, LibroDTO request) {
        libro.setTitulo(request.titulo().trim());
        libro.setAutor(request.autor().trim());
        libro.setIsbn(request.isbn().trim());
        libro.setStock(request.stock());
    }
}
