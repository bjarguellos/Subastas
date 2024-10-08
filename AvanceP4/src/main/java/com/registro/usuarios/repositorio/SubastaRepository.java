package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Subasta;
import com.registro.usuarios.modelo.EstadoSubasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// El repositorio SubastaRepository es una interfaz que extiende 
// JpaRepository para gestionar la persistencia de entidades Subasta 
// en la base de datos. Proporciona métodos para realizar operaciones 
// de consulta y actualización sobre las subastas, basadas en el estado y las fechas relevantes.

@Repository
public interface SubastaRepository extends JpaRepository<Subasta, Long> {
        List<Subasta> findByEstado(EstadoSubasta estado);

        // Actualiza el estado de las subastas que han terminado
        @Modifying
        @Transactional
        @Query("UPDATE Subasta s SET s.estado = :estado WHERE s.fechaFin < :now AND s.estado = :estadoActiva")
        void updateEstadoFinalizadas(LocalDateTime now, EstadoSubasta estado, EstadoSubasta estadoActiva);

        List<Subasta> findByEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        EstadoSubasta estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

        List<Subasta> findByEstadoAndFechaInicioGreaterThanEqual(
                        EstadoSubasta estado, LocalDateTime fechaInicio);

}
