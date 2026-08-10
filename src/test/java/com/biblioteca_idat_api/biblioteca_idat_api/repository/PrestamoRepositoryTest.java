package com.biblioteca_idat_api.biblioteca_idat_api.repository;

import com.biblioteca_idat_api.biblioteca_idat_api.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PrestamoRepositoryTest {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Test
    void debeDetectarPrestamoActivoDuplicado() {
        // Arrange: creamos un usuario y un libro de prueba
        Usuario usuario = new Usuario();
        usuario.setUsername("jperez");
        usuario.setNombre("Juan Pérez");
        usuario.setEmail("jperez@test.com");
        usuario.setPassword("hash-de-prueba");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Libro libro = new Libro();
        libro.setTitulo("Cien años de soledad");
        libro.setAutor("Gabriel García Márquez");
        libro.setIsbn("978-3-16-148410-0");
        libro.setStock(3);
        libro.setStockDisponible(3);
        libro = libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(7));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamoRepository.save(prestamo);

        // Act
        boolean existeActivo = prestamoRepository.existsByUsuarioIdAndLibroIdAndEstado(
                usuario.getId(), libro.getId(), EstadoPrestamo.ACTIVO);

        boolean existeDevuelto = prestamoRepository.existsByUsuarioIdAndLibroIdAndEstado(
                usuario.getId(), libro.getId(), EstadoPrestamo.DEVUELTO);

        // Assert: la regla de negocio "no repetir préstamo activo" es detectable
        assertThat(existeActivo).isTrue();
        assertThat(existeDevuelto).isFalse();
    }

    @Test
    void debeListarPrestamosDeUnUsuarioOrdenadosPorFecha() {
        Usuario usuario = new Usuario();
        usuario.setUsername("mgarcia");
        usuario.setNombre("María García");
        usuario.setEmail("mgarcia@test.com");
        usuario.setPassword("hash-de-prueba");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Libro libro = new Libro();
        libro.setTitulo("1984");
        libro.setAutor("George Orwell");
        libro.setIsbn("978-0-452-28423-4");
        libro.setStock(2);
        libro.setStockDisponible(2);
        libro = libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now().minusDays(10));
        prestamo.setFechaDevolucionEsperada(LocalDate.now().minusDays(3));
        prestamo.setFechaDevolucionReal(LocalDate.now().minusDays(2));
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamoRepository.save(prestamo);

        var historial = prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuario.getId());

        assertThat(historial).hasSize(1);
        assertThat(historial.get(0).getLibro().getTitulo()).isEqualTo("1984");
    }
}