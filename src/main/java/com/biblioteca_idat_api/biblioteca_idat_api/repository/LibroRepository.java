package com.biblioteca_idat_api.biblioteca_idat_api.repository;

import com.biblioteca_idat_api.biblioteca_idat_api.entity.Libro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// LibroRepository.java
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    List<Libro> findByAutorContainingIgnoreCase(String autor);

    @Query("SELECT l FROM Libro l WHERE l.stockDisponible > 0")
    List<Libro> findDisponibles();

    // Bloqueo pesimista: evita condiciones de carrera cuando dos usuarios
    // intentan prestar el último ejemplar disponible al mismo tiempo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Libro l WHERE l.id = :id")
    Optional<Libro> findByIdForUpdate(@Param("id") Long id);
}