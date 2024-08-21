package com.registro.usuarios.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.modelo.Usuario;

/**
 * La interfaz UsuarioServicio extiende la interfaz UserDetailsService de 
 * Spring Security para proporcionar autenticación y autorización. 
 * Define los métodos necesarios para gestionar las operaciones relacionadas 
 * con la entidad Usuario en la aplicación.
 */
public interface UsuarioServicio extends UserDetailsService {

    /**
     * Guarda un nuevo usuario en la base de datos utilizando la información 
     * proporcionada en el DTO de registro.
     *
     * @param registroDTO El objeto DTO que contiene los datos del usuario 
     *                    que se desea registrar.
     * @return El objeto Usuario que ha sido guardado en la base de datos.
     */
    @Autowired
    Usuario guardar(UsuarioRegistroDTO registroDTO);

    /**
     * Devuelve una lista de todos los usuarios registrados en el sistema.
     *
     * @return Una lista de objetos Usuario.
     */
    List<Usuario> listarUsuarios();

    /**
     * Elimina un usuario de la base de datos por su ID.
     *
     * @param id El ID del usuario que se desea eliminar.
     */
    void deleteById(Long id);

    /**
     * Busca un usuario por su ID en la base de datos.
     *
     * @param id El ID del usuario que se desea buscar.
     * @return Un Optional que contiene el usuario encontrado, si existe.
     */
    Optional<Usuario> findById(Long id);

    /**
     * Guarda un usuario existente con su ID. Este método puede ser utilizado 
     * para actualizar la información de un usuario.
     *
     * @param usuario El objeto Usuario que se desea guardar o actualizar.
     * @return El objeto Usuario que ha sido guardado o actualizado.
     */
    Usuario guardarById(Usuario usuario);

    /**
     * Encuentra un usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario que se 
     *              desea buscar.
     * @return El objeto Usuario correspondiente al email proporcionado.
     */
    Usuario findByEmail(String email);

}
