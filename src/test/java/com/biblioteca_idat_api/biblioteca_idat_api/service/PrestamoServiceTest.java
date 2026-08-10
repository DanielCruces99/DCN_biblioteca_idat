package com.biblioteca_idat_api.biblioteca_idat_api.service;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.PrestamoRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.EstadoPrestamo;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Libro;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Prestamo;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.LibroRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.PrestamoRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PrestamoServiceTest {

    @Autowired private PrestamoService prestamoService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private LibroRepository libroRepository;
    @Autowired private PrestamoRepository prestamoRepository;

    private Usuario usuario;
    private Libro libro;

    @BeforeEach
    void prepararDatos() {
        usuario = new Usuario();
        usuario.setUsername("prestamo.test");
        usuario.setNombre("Usuario Préstamo");
        usuario.setEmail("prestamo@test.com");
        usuario.setPassword("hash");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        libro = new Libro();
        libro.setTitulo("Libro de prueba");
        libro.setAutor("Autor de prueba");
        libro.setIsbn("TEST-ISBN-001");
        libro.setStock(2);
        libro.setStockDisponible(2);
        libro = libroRepository.save(libro);
    }

    @Test
    void crearYDevolverPrestamoDebeMantenerElStockConsistente() {
        var creado = prestamoService.crearPrestamo(
                new PrestamoRequestDTO(usuario.getId(), libro.getId(), 7));

        assertThat(libroRepository.findById(libro.getId()).orElseThrow().getStockDisponible()).isEqualTo(1);

        var devuelto = prestamoService.registrarDevolucion(creado.id());

        assertThat(devuelto.estado()).isEqualTo("DEVUELTO");
        assertThat(libroRepository.findById(libro.getId()).orElseThrow().getStockDisponible()).isEqualTo(2);
    }

    @Test
    void consultarHistorialDebeMarcarComoAtrasadosLosPrestamosVencidos() {
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now().minusDays(10));
        prestamo.setFechaDevolucionEsperada(LocalDate.now().minusDays(3));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamoRepository.saveAndFlush(prestamo);

        var historial = prestamoService.listarPorUsuario(usuario.getId());

        assertThat(historial).singleElement().extracting(item -> item.estado()).isEqualTo("ATRASADO");
    }
}
