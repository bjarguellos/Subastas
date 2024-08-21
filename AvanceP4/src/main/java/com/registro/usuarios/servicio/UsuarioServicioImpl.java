package com.registro.usuarios.servicio;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.repositorio.UsuarioRepositorio;

/**
 * La clase UsuarioServicioImpl implementa la interfaz UsuarioServicio y 
 * proporciona la lógica para manejar las operaciones relacionadas con la 
 * entidad Usuario, incluyendo el registro de nuevos usuarios, la gestión de 
 * roles y la carga de detalles de usuario para la autenticación.
 */
@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    // Repositorio para interactuar con la base de datos de usuarios
    private final UsuarioRepositorio usuarioRepositorio;

    // Codificador de contraseñas para cifrar las contraseñas de los usuarios
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * 
     * @param usuarioRepositorio El repositorio de usuarios que se utilizará para
     *                           las operaciones CRUD.
     */
    @Autowired
    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Guarda un nuevo usuario en la base de datos. El usuario registrado tendrá 
     * un rol predeterminado de "USER" y su contraseña será cifrada antes de 
     * guardarse.
     * 
     * @param registroDTO El DTO que contiene la información del usuario a registrar.
     * @return El usuario que ha sido guardado en la base de datos.
     */
    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = new Usuario(registroDTO.getNombre(),
                registroDTO.getApellido(), registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()), Arrays.asList(new Rol("USER")));
        return usuarioRepositorio.save(usuario);
    }

    /**
     * Carga un usuario por su nombre de usuario (email) para la autenticación. 
     * Si no se encuentra un usuario con el email proporcionado, se lanza una 
     * excepción UsernameNotFoundException.
     * 
     * @param username El email del usuario a cargar.
     * @return Un objeto UserDetails que contiene la información del usuario.
     * @throws UsernameNotFoundException Si no se encuentra el usuario con el email proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByEmail(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario o password inválidos");
        }
        return new User(usuario.getEmail(), usuario.getPassword(), mapearAutoridadesRoles(usuario.getRoles()));
    }

    /**
     * Mapea los roles de un usuario a las autoridades de Spring Security.
     * 
     * @param roles Los roles del usuario.
     * @return Una colección de GrantedAuthority que representa los roles del usuario.
     */
    private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los usuarios registrados en la base de datos.
     * 
     * @return Una lista de objetos Usuario.
     */
    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     * 
     * @param id El ID del usuario que se desea eliminar.
     */
    @Override
    public void deleteById(Long id) {
        usuarioRepositorio.deleteById(id);
    }

    /**
     * Busca un usuario por su ID en la base de datos.
     * 
     * @param id El ID del usuario que se desea buscar.
     * @return Un Optional que contiene el usuario encontrado, si existe.
     */
    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepositorio.findById(id);
    }

    /**
     * Guarda o actualiza un usuario existente en la base de datos.
     * 
     * @param usuario El objeto Usuario que se desea guardar o actualizar.
     * @return El objeto Usuario que ha sido guardado o actualizado.
     */
    @Override
    public Usuario guardarById(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    /**
     * Encuentra un usuario por su dirección de correo electrónico.
     * 
     * @param email La dirección de correo electrónico del usuario que se 
     *              desea buscar.
     * @return El objeto Usuario correspondiente al email proporcionado.
     */
    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }
}
