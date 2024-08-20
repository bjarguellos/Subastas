package com.registro.usuarios.controlador;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.servicio.UsuarioServicio;

@Controller
public class RegistroControlador {

    private final UsuarioServicio usuarioServicio;

    @Autowired
    public RegistroControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/login")
    public String iniciarSesion() {
        return "login";
    }

    @GetMapping("/info")
    public String mostrarInfo() {
        return "info";
    }

    @GetMapping("/")
    public String perfil(Model modelo) {
        // Obtiene la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Obtiene el email del usuario autenticado

        // Busca el usuario por su email para obtener el ID
        Usuario usuario = usuarioServicio.findByEmail(email);
        if (usuario != null) {
            // Añade el usuario al modelo para la vista
            modelo.addAttribute("usuario", usuario);
            return "PerfilUsuario"; // Devuelve la vista del perfil del usuario
        } else {
            return "redirect:/login"; // Redirige a la página de login si el usuario no se encuentra
        }
    }

    @GetMapping("/editarPerfil")
    public String mostrarFormularioEdicion(Model modelo) {
        // Obtiene la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Obtiene el email del usuario autenticado

        // Busca el usuario por su email para obtener el ID
        Usuario usuario = usuarioServicio.findByEmail(email);
        if (usuario != null) {
            // Añade el usuario al modelo para la vista
            modelo.addAttribute("usuario", usuario);
            return "PerfilEditar"; // Devuelve la vista del formulario de edición
        } else {
            return "redirect:/PerfilEditar"; // Redirige a la página de login si el usuario no se encuentra
        }
    }

    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(@ModelAttribute("usuario") Usuario usuarioDetails, Model modelo) {
        // Obtiene la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Obtiene el email del usuario autenticado

        // Busca el usuario por su email para obtener el ID
        Usuario usuario = usuarioServicio.findByEmail(email);
        if (usuario != null) {
            // Solo actualiza el nombre y el apellido, manteniendo el rol existente
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setApellido(usuarioDetails.getApellido());
            // El rol se mantiene igual, por lo que no lo tocamos aquí

            // Guarda los cambios en la base de datos
            usuarioServicio.guardarById(usuario); // Asegúrate de que el método guardar actualice el usuario en la DB

            // Añade el usuario actualizado al modelo para la vista
            modelo.addAttribute("usuario", usuario);
            return "redirect:/"; // Redirige a la página del perfil del usuario
        } else {
            return "redirect:/PerfilUsuario"; // Redirige a la página de perfil si el usuario no se encuentra
        }
    }

    @GetMapping("/ListaSubastas")
    public String verPaginaDeUsuariosSubastas() {
        return "ListaSubastas";
    }

    @GetMapping("/homeAdmin")
    public String verAdmin(Model modelo) {
        modelo.addAttribute("usuarios", usuarioServicio.listarUsuarios());
        return "homeAdmin";
    }

    @GetMapping("/editar/{id}")
    public String mostrarUsuario(@PathVariable Long id, Model model) {
        // Busca el usuario por su ID
        Optional<Usuario> usuario = usuarioServicio.findById(id);
        if (usuario.isPresent()) {
            // Añade el usuario al modelo si se encuentra
            model.addAttribute("usuario", usuario.get());
            return "usuarioEditar"; // Devuelve la vista de edición de usuario
        } else {
            // Redirige a la página de administración si no se encuentra el usuario
            return "redirect:/homeAdmin";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String updatePersona(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuarioDetails) {
        // Busca el usuario por su ID
        Optional<Usuario> optionalUsuario = usuarioServicio.findById(id);
        if (optionalUsuario.isPresent()) {
            // Obtiene el usuario y actualiza sus detalles
            Usuario usuario = optionalUsuario.get();
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setApellido(usuarioDetails.getApellido());
            usuario.setRoles(usuarioDetails.getRoles());
            usuarioServicio.guardarById(usuario); // Guarda el usuario actualizado
            return "redirect:/homeAdmin"; // Redirige a la página de administración
        } else {
            // Redirige a la página de administración si no se encuentra el usuario
            return "redirect:/homeAdmin";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioServicio.deleteById(id); // Elimina el usuario por su ID
        return "redirect:/homeAdmin"; // Redirige a la página de administración después de eliminar
    }

    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam(name = "email", required = false) String email, Model model) {
        if (email != null && !email.isEmpty()) {
            Usuario usuario = usuarioServicio.findByEmail(email);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            } else {
                model.addAttribute("error", "Usuario no encontrado");
            }
        } else {
            model.addAttribute("error", "El correo electrónico no puede estar vacío");
        }
        return "homeAdmin"; // Nombre de la vista donde se muestran los resultados
    }
}
