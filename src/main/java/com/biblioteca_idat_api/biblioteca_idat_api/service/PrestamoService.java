package com.biblioteca_idat_api.biblioteca_idat_api.service;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.*;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.*;
import com.biblioteca_idat_api.biblioteca_idat_api.mapper.PrestamoMapper;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.LibroRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.PrestamoRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           UsuarioRepository usuarioRepository,
                           LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;

    }
    @Transactional
    public List<PrestamoResponseDTO> listarPorUsuario(Long usuarioId) {
        actualizarPrestamosVencidos();
        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuarioId).stream()
                .map(PrestamoMapper::toDTO)
                .toList();
    }


    @Transactional
    public PrestamoResponseDTO crearPrestamo(PrestamoRequestDTO request) {
        actualizarPrestamosVencidos();

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado: " + request.usuarioId()));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("No se puede registrar un préstamo para un usuario inactivo");
        }

        Libro libro = libroRepository.findByIdForUpdate(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Libro no encontrado: " + request.libroId()));

        if (prestamoRepository.existsByUsuarioIdAndLibroIdAndEstadoIn(
                request.usuarioId(), request.libroId(),
                List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.ATRASADO))) {
            throw new PrestamoDuplicadoException(
                    "El usuario ya tiene un préstamo activo de este libro");
        }

        if (libro.getStockDisponible() <= 0) {
            throw new StockInsuficienteException(
                    "No hay ejemplares disponibles de: " + libro.getTitulo());
        }

        libro.setStockDisponible(libro.getStockDisponible() - 1);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(request.diasPlazo()));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo guardado = prestamoRepository.save(prestamo);
        return PrestamoMapper.toDTO(guardado);
    }

    @Transactional
    public PrestamoResponseDTO registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findByIdForUpdate(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Préstamo no encontrado: " + prestamoId));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new IllegalStateException("Este préstamo ya fue devuelto");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(Math.min(libro.getStock(), libro.getStockDisponible() + 1));

        Prestamo guardado = prestamoRepository.save(prestamo);
        return PrestamoMapper.toDTO(guardado);
    }

    private void actualizarPrestamosVencidos() {
        prestamoRepository.marcarVencidos(
                LocalDate.now(), EstadoPrestamo.ACTIVO, EstadoPrestamo.ATRASADO);
    }
}
