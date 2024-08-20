package com.registro.usuarios.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.modelo.Usuario;

public interface UsuarioServicio extends UserDetailsService {

    @Autowired
    Usuario guardar(UsuarioRegistroDTO registroDTO);

    List<Usuario> listarUsuarios();

    void deleteById(Long id);

    Optional<Usuario> findById(Long id);

    Usuario guardarById(Usuario usuario);

    // Nuevo método para encontrar un usuario por email
    Usuario findByEmail(String email);

}
