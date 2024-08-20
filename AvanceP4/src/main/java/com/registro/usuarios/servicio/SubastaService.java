package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Subasta;

import com.registro.usuarios.modelo.EstadoSubasta;

import java.math.BigDecimal;
import java.util.List;

public interface SubastaService {
    Subasta crearSubasta(Subasta subasta);

    Subasta actualizarSubasta(Subasta subasta);

    Subasta obtenerSubastaPorId(Long id);

    void eliminarSubasta(Long id);

    List<Subasta> obtenerSubastasPorEstado(EstadoSubasta estado);

    List<Subasta> obtenerSubastasActivas();

    List<Subasta> obtenerSubastasDisponibles();

    List<Subasta> obtenerSubastasFinalizadas();

    void realizarPuja(Long subastaId, BigDecimal cantidadPuja);

    List<Subasta> vereditarSubastas();

}
