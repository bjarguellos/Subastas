package com.registro.usuarios.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.registro.usuarios.modelo.Usuario;

// El repositorio UsuarioRepositorio es una interfaz que extiende 
// JpaRepository para manejar la persistencia de entidades Usuario 
// en la base de datos. Permite realizar operaciones CRUD sobre los 
// usuarios y ofrece métodos adicionales para consultas personalizadas.

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

	public Usuario findByEmail(String email);

}
