package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Subasta;
import com.registro.usuarios.modelo.EstadoSubasta;
import com.registro.usuarios.repositorio.SubastaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubastaServicioImpl implements SubastaService {

    @Autowired
    private SubastaRepository subastaRepository;

    @Override
    public Subasta crearSubasta(Subasta subasta) {
        BigDecimal precioInicial = subasta.getPrecioInicial();
        if (precioInicial == null || precioInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El artículo debe tener un precio inicial válido.");
        }

        if (esObjetoRestringido(subasta.getArticulo())) {
            throw new IllegalArgumentException("El artículo está restringido y no puede ser subastado.");
        }

        subasta.setEstado(EstadoSubasta.DISPONIBLE);
        return subastaRepository.save(subasta);
    }

    @Override
    public Subasta actualizarSubasta(Subasta subasta) {
        return subastaRepository.save(subasta);
    }

    @Override
    public Subasta obtenerSubastaPorId(Long id) {
        return subastaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subasta no encontrada con el ID: " + id));
    }

    @Override
    public void eliminarSubasta(Long id) {
        Subasta subasta = obtenerSubastaPorId(id);
        if (subasta != null) {
            subasta.setEstado(EstadoSubasta.FINALIZADA);
            subastaRepository.save(subasta);
        } else {
            throw new IllegalArgumentException("Subasta no encontrada con el ID: " + id);
        }
    }

    @Override
    public List<Subasta> obtenerSubastasPorEstado(EstadoSubasta estado) {
        return subastaRepository.findByEstado(estado);
    }

    @Override
    public List<Subasta> obtenerSubastasActivas() {
        LocalDateTime now = LocalDateTime.now();
        List<Subasta> todasSubastas = subastaRepository.findAll();

        for (Subasta subasta : todasSubastas) {
            if (subasta.getFechaInicio().isBefore(now) && EstadoSubasta.DISPONIBLE.equals(subasta.getEstado())) {
                subasta.setEstado(EstadoSubasta.ACTIVA);
                subastaRepository.save(subasta);
            }
        }

        return subastaRepository.findByEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                EstadoSubasta.ACTIVA, now, now);
    }

    @Override
    public List<Subasta> obtenerSubastasDisponibles() {
        LocalDateTime now = LocalDateTime.now();
        List<Subasta> todasSubastas = subastaRepository.findAll();

        for (Subasta subasta : todasSubastas) {
            if (subasta.getFechaInicio().isBefore(now) && EstadoSubasta.DISPONIBLE.equals(subasta.getEstado())) {
                subasta.setEstado(EstadoSubasta.ACTIVA);
                subastaRepository.save(subasta);
            }
        }

        return subastaRepository.findByEstadoAndFechaInicioGreaterThanEqual(EstadoSubasta.DISPONIBLE, now);
    }

    @Override
    public List<Subasta> obtenerSubastasFinalizadas() {
        return subastaRepository.findByEstado(EstadoSubasta.FINALIZADA);
    }

    private boolean esObjetoRestringido(String articulo) {
        String[] objetosRestringidos = { "arma", "explosivo", "sustancia psicoactiva" };
        for (String objeto : objetosRestringidos) {
            if (articulo.toLowerCase().contains(objeto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void realizarPuja(Long subastaId, BigDecimal cantidadPuja) {
        Subasta subasta = obtenerSubastaPorId(subastaId);
        if (subasta != null && EstadoSubasta.ACTIVA.equals(subasta.getEstado())) {
            if (subasta.getPrecioActual() == null || cantidadPuja.compareTo(subasta.getPrecioActual()) > 0) {
                subasta.setPrecioActual(cantidadPuja);
                actualizarSubasta(subasta);
            } else {
                throw new IllegalArgumentException("La cantidad de la puja debe ser mayor que el precio actual.");
            }
        } else {
            throw new IllegalArgumentException("La subasta no está activa o no se encontró.");
        }
    }

    @Override
    public List<Subasta> vereditarSubastas() {
        return subastaRepository.findAll();
    }

}
