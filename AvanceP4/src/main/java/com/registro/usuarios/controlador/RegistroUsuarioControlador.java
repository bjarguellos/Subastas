package com.registro.usuarios.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.servicio.UsuarioServicio;

/**
 * Controlador para manejar las solicitudes relacionadas con el registro de usuarios.
 * Este controlador gestiona las vistas de registro y el procesamiento de los datos 
 * enviados por los usuarios para crear una nueva cuenta en el sistema.
 */
@Controller
@RequestMapping("/registro")
public class RegistroUsuarioControlador {

    /**
     * Servicio que proporciona las operaciones necesarias para manejar usuarios.
     */
    private UsuarioServicio usuarioServicio;

    /**
     * Constructor que inyecta el servicio de usuario.
     * 
     * @param usuarioServicio servicio para gestionar las operaciones de usuario.
     */
    public RegistroUsuarioControlador(UsuarioServicio usuarioServicio) {
        super();
        this.usuarioServicio = usuarioServicio;
    }

    /**
     * Método que añade un nuevo objeto UsuarioRegistroDTO al modelo de la vista.
     * 
     * @return un nuevo objeto UsuarioRegistroDTO.
     */
    @ModelAttribute("usuario")
    public UsuarioRegistroDTO retornarNuevoUsuarioRegistroDTO() {
        return new UsuarioRegistroDTO();
    }

    /**
     * Maneja las solicitudes GET a la ruta "/registro".
     * 
     * @return el nombre de la vista del formulario de registro.
     */
    @GetMapping
    public String mostrarFormularioDeRegistro() {
        return "registro";
    }

    /**
     * Maneja las solicitudes POST para registrar un nuevo usuario.
     * 
     * @param registroDTO datos del usuario obtenidos del formulario de registro.
     * @return redirecciona a la página de registro con un mensaje de éxito.
     */
    @PostMapping
    public String registrarCuentaDeUsuario(@ModelAttribute("usuario") UsuarioRegistroDTO registroDTO) {
        usuarioServicio.guardar(registroDTO);
        return "redirect:/registro?exito";
    }
}
