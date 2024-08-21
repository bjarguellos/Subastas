package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Subasta;
import com.registro.usuarios.modelo.EstadoSubasta;
import com.registro.usuarios.repositorio.SubastaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la interfaz SubastaService que proporciona los servicios 
 * necesarios para gestionar las subastas en la aplicación.
 */
@Service
public class SubastaServicioImpl implements SubastaService {

    @Autowired
    private SubastaRepository subastaRepository;

    /**
     * Crea una nueva subasta y la guarda en la base de datos.
     *
     * @param subasta El objeto Subasta que se desea crear.
     * @return La subasta creada y guardada en la base de datos.
     * @throws IllegalArgumentException Si el precio inicial no es válido o si el artículo está restringido.
     */
    @Override
    public Subasta crearSubasta(Subasta subasta) {
        BigDecimal precioInicial = subasta.getPrecioInicial();
        
        // Verificar que el precio inicial sea válido.
        if (precioInicial == null || precioInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El artículo debe tener un precio inicial válido.");
        }

        // Verificar si el artículo está restringido.
        if (esObjetoRestringido(subasta.getArticulo())) {
            throw new IllegalArgumentException("El artículo está restringido y no puede ser subastado.");
        }

        // Establecer el estado inicial de la subasta como DISPONIBLE.
        subasta.setEstado(EstadoSubasta.DISPONIBLE);
        return subastaRepository.save(subasta);
    }

    /**
     * Actualiza una subasta existente en la base de datos.
     *
     * @param subasta El objeto Subasta con la información actualizada.
     * @return La subasta actualizada y guardada en la base de datos.
     */
    @Override
    public Subasta actualizarSubasta(Subasta subasta) {
        return subastaRepository.save(subasta);
    }

    /**
     * Obtiene una subasta por su ID.
     *
     * @param id El ID de la subasta.
     * @return La subasta encontrada o una excepción si no se encuentra.
     * @throws IllegalArgumentException Si la subasta no se encuentra.
     */
    @Override
    public Subasta obtenerSubastaPorId(Long id) {
        return subastaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subasta no encontrada con el ID: " + id));
    }

    /**
     * Elimina una subasta estableciendo su estado como FINALIZADA.
     *
     * @param id El ID de la subasta que se desea eliminar.
     * @throws IllegalArgumentException Si la subasta no se encuentra.
     */
    @Override
    public void eliminarSubasta(Long id) {
        Subasta subasta = obtenerSubastaPorId(id);
        if (subasta != null) {
            // Establecer el estado de la subasta como FINALIZADA.
            subasta.setEstado(EstadoSubasta.FINALIZADA);
            subastaRepository.save(subasta);
        } else {
            throw new IllegalArgumentException("Subasta no encontrada con el ID: " + id);
        }
    }

    /**
     * Obtiene una lista de subastas por su estado.
     *
     * @param estado El estado de las subastas que se desean obtener.
     * @return Una lista de subastas que tienen el estado especificado.
     */
    @Override
    public List<Subasta> obtenerSubastasPorEstado(EstadoSubasta estado) {
        return subastaRepository.findByEstado(estado);
    }

    /**
     * Obtiene una lista de subastas activas, actualizando el estado si es necesario.
     *
     * @return Una lista de subastas activas.
     */
    @Override
    public List<Subasta> obtenerSubastasActivas() {
        LocalDateTime now = LocalDateTime.now();
        List<Subasta> todasSubastas = subastaRepository.findAll();

        // Actualizar el estado de las subastas que deberían estar activas.
        for (Subasta subasta : todasSubastas) {
            if (subasta.getFechaInicio().isBefore(now) && EstadoSubasta.DISPONIBLE.equals(subasta.getEstado())) {
                subasta.setEstado(EstadoSubasta.ACTIVA);
                subastaRepository.save(subasta);
            }
        }

        // Devolver las subastas activas en este momento.
        return subastaRepository.findByEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                EstadoSubasta.ACTIVA, now, now);
    }

    /**
     * Obtiene una lista de subastas disponibles que aún no han comenzado.
     *
     * @return Una lista de subastas disponibles.
     */
    @Override
    public List<Subasta> obtenerSubastasDisponibles() {
        LocalDateTime now = LocalDateTime.now();
        List<Subasta> todasSubastas = subastaRepository.findAll();

        // Actualizar el estado de las subastas que deberían estar activas.
        for (Subasta subasta : todasSubastas) {
            if (subasta.getFechaInicio().isBefore(now) && EstadoSubasta.DISPONIBLE.equals(subasta.getEstado())) {
                subasta.setEstado(EstadoSubasta.ACTIVA);
                subastaRepository.save(subasta);
            }
        }

        // Devolver las subastas disponibles.
        return subastaRepository.findByEstadoAndFechaInicioGreaterThanEqual(EstadoSubasta.DISPONIBLE, now);
    }

    /**
     * Obtiene una lista de subastas finalizadas.
     *
     * @return Una lista de subastas que han sido finalizadas.
     */
    @Override
    public List<Subasta> obtenerSubastasFinalizadas() {
        return subastaRepository.findByEstado(EstadoSubasta.FINALIZADA);
    }

    /**
     * Verifica si un artículo está restringido para subastas.
     *
     * @param articulo El nombre del artículo que se desea verificar.
     * @return true si el artículo está restringido, de lo contrario false.
     */
    private boolean esObjetoRestringido(String articulo) {
        String[] objetosRestringidos = { "arma", "explosivo", "sustancia psicoactiva" };
        for (String objeto : objetosRestringidos) {
            if (articulo.toLowerCase().contains(objeto)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Realiza una puja en una subasta, actualizando el precio actual si es mayor.
     *
     * @param subastaId El ID de la subasta en la que se realiza la puja.
     * @param cantidadPuja La cantidad ofrecida en la puja.
     * @throws IllegalArgumentException Si la puja no es válida o la subasta no está activa.
     */
    @Override
    public void realizarPuja(Long subastaId, BigDecimal cantidadPuja) {
        Subasta subasta = obtenerSubastaPorId(subastaId);
        if (subasta != null && EstadoSubasta.ACTIVA.equals(subasta.getEstado())) {
            // Verificar que la puja sea mayor que el precio actual.
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

    /**
     * Devuelve una lista de todas las subastas.
     *
     * @return Una lista de todas las subastas en la base de datos.
     */
    @Override
    public List<Subasta> vereditarSubastas() {
        return subastaRepository.findAll();
    }

}
