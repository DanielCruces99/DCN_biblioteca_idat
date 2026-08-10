package com.biblioteca_idat_api.biblioteca_idat_api.repository;

import com.biblioteca_idat_api.biblioteca_idat_api.entity.EstadoPrestamo;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Collection;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);

    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    boolean existsByUsuarioIdAndLibroIdAndEstado(Long usuarioId, Long libroId, EstadoPrestamo estado);

    boolean existsByUsuarioIdAndLibroIdAndEstadoIn(
            Long usuarioId, Long libroId, Collection<EstadoPrestamo> estados);

    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEsperada < :hoy")
    List<Prestamo> findVencidos(@Param("hoy") LocalDate hoy);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
           UPDATE Prestamo p SET p.estado = :atrasado
           WHERE p.estado = :activo AND p.fechaDevolucionEsperada < :hoy
           """)
    int marcarVencidos(@Param("hoy") LocalDate hoy,
                       @Param("activo") EstadoPrestamo activo,
                       @Param("atrasado") EstadoPrestamo atrasado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prestamo p WHERE p.id = :id")
    Optional<Prestamo> findByIdForUpdate(@Param("id") Long id);

    @Query("""
           SELECT p FROM Prestamo p
           JOIN FETCH p.usuario u
           JOIN FETCH p.libro l
           WHERE p.estado = :estado
           """)
    List<Prestamo> findByEstadoConDetalle(@Param("estado") EstadoPrestamo estado);

    long countByLibroIdAndEstado(Long libroId, EstadoPrestamo estado);

    long countByLibroId(Long libroId);
}
