package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Subasta;
import com.registro.usuarios.modelo.EstadoSubasta;

import java.math.BigDecimal;
import java.util.List;

/**
 * La interfaz SubastaService define los métodos que deben 
 * ser implementados para gestionar las operaciones relacionadas 
 * con la entidad Subasta. Proporciona una capa de abstracción sobre 
 * los repositorios para interactuar con las subastas en la aplicación.
 */
public interface SubastaService {

    /**
     * Crea una nueva subasta.
     *
     * @param subasta El objeto Subasta que se desea crear.
     * @return La subasta creada.
     */
    Subasta crearSubasta(Subasta subasta);

    /**
     * Actualiza una subasta existente.
     *
     * @param subasta El objeto Subasta con la información actualizada.
     * @return La subasta actualizada.
     */
    Subasta actualizarSubasta(Subasta subasta);

    /**
     * Obtiene una subasta por su ID.
     *
     * @param id El ID de la subasta que se desea obtener.
     * @return La subasta correspondiente al ID proporcionado.
     */
    Subasta obtenerSubastaPorId(Long id);

    /**
     * Elimina una subasta por su ID. Generalmente, esto podría 
     * implicar marcar la subasta como finalizada en lugar de 
     * eliminarla físicamente de la base de datos.
     *
     * @param id El ID de la subasta que se desea eliminar.
     */
    void eliminarSubasta(Long id);

    /**
     * Obtiene una lista de subastas que están en un estado específico.
     *
     * @param estado El estado de las subastas que se desean obtener.
     * @return Una lista de subastas con el estado especificado.
     */
    List<Subasta> obtenerSubastasPorEstado(EstadoSubasta estado);

    /**
     * Obtiene una lista de subastas activas. Una subasta activa es 
     * aquella que ha comenzado pero aún no ha finalizado.
     *
     * @return Una lista de subastas activas.
     */
    List<Subasta> obtenerSubastasActivas();

    /**
     * Obtiene una lista de subastas disponibles. Una subasta disponible 
     * es aquella que está programada para comenzar pero aún no ha 
     * alcanzado su fecha de inicio.
     *
     * @return Una lista de subastas disponibles.
     */
    List<Subasta> obtenerSubastasDisponibles();

    /**
     * Obtiene una lista de subastas finalizadas. Una subasta finalizada 
     * es aquella que ha alcanzado su fecha de fin o ha sido cerrada 
     * manualmente.
     *
     * @return Una lista de subastas finalizadas.
     */
    List<Subasta> obtenerSubastasFinalizadas();

    /**
     * Realiza una puja en una subasta específica. La cantidad de la puja 
     * debe ser mayor que el precio actual de la subasta.
     *
     * @param subastaId El ID de la subasta en la que se desea realizar la puja.
     * @param cantidadPuja La cantidad de la puja que se desea ofrecer.
     */
    void realizarPuja(Long subastaId, BigDecimal cantidadPuja);

    /**
     * Devuelve una lista de todas las subastas registradas en el sistema.
     * Este método puede ser utilizado para ver o editar subastas.
     *
     * @return Una lista de todas las subastas.
     */
    List<Subasta> vereditarSubastas();

}
