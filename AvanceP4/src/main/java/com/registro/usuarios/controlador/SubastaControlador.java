package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Subasta;
import com.registro.usuarios.servicio.SubastaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para gestionar las operaciones relacionadas con las subastas.
 * Este controlador maneja la creación, edición, eliminación y visualización de subastas,
 * así como la colocación de pujas en las subastas activas.
 */
@Controller
public class SubastaControlador {

    /**
     * Servicio que proporciona las operaciones necesarias para manejar subastas.
     */
    @Autowired
    private SubastaService subastaServicio;

    /**
     * Configura un Binder para convertir cadenas de texto a objetos LocalDateTime.
     * 
     * @param binder objeto WebDataBinder que se utiliza para registrar los editores personalizados.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Formato de fecha inválido");
                }
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value) : "";
            }
        });
    }

    /**
     * Muestra el formulario para la creación de una nueva subasta.
     * 
     * @param model objeto Model que se utiliza para pasar datos a la vista.
     * @return el nombre de la vista del formulario de creación de subastas.
     */
    @GetMapping("/crearSubasta")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("subasta", new Subasta());
        return "crearSubasta";
    }

    /**
     * Procesa la solicitud para crear una nueva subasta.
     * 
     * @param subasta objeto Subasta que contiene los datos de la nueva subasta.
     * @param redirectAttributes atributos que se envían a la vista después de la redirección.
     * @return redirecciona a la lista de subastas después de crear la subasta.
     */
    @PostMapping("/crearSubasta")
    public String crearSubasta(@ModelAttribute("subasta") Subasta subasta, RedirectAttributes redirectAttributes) {
        if (esObjetoRestringido(subasta.getArticulo())) {
            redirectAttributes.addFlashAttribute("error", "El artículo está restringido.");
            return "redirect:/crearSubasta";
        }

        subastaServicio.crearSubasta(subasta);
        redirectAttributes.addFlashAttribute("mensaje", "Subasta creada exitosamente.");
        return "redirect:/listaSubastas";
    }

    /**
     * Verifica si un artículo está restringido para ser subastado.
     * 
     * @param articulo nombre del artículo a verificar.
     * @return verdadero si el artículo está restringido, falso en caso contrario.
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
     * Muestra la lista de subastas disponibles, activas y finalizadas.
     * 
     * @param model objeto Model que se utiliza para pasar datos a la vista.
     * @return el nombre de la vista que muestra la lista de subastas.
     */
    @GetMapping("/listaSubastas")
    public String verListaSubastas(Model model) {
        List<Subasta> subastasDisponibles = subastaServicio.obtenerSubastasDisponibles();
        List<Subasta> subastasActivas = subastaServicio.obtenerSubastasActivas();
        List<Subasta> subastasFinalizadas = subastaServicio.obtenerSubastasFinalizadas();
        model.addAttribute("subastasDisponibles", subastasDisponibles);
        model.addAttribute("subastasActivas", subastasActivas);
        model.addAttribute("subastasFinalizadas", subastasFinalizadas);
        return "listaSubastas";
    }

    /**
     * Procesa la solicitud para realizar una puja en una subasta.
     * 
     * @param auctionId ID de la subasta en la que se desea pujar.
     * @param bidAmount cantidad de la puja.
     * @param model objeto Model que se utiliza para pasar datos a la vista.
     * @return redirecciona a la lista de subastas si la puja es exitosa o 
     *         devuelve la misma vista si hay un error.
     */
    @PostMapping("/placeBid")
    public String realizarPuja(@RequestParam("auctionId") Long auctionId,
            @RequestParam("bidAmount") BigDecimal bidAmount, Model model) {
        try {
            subastaServicio.realizarPuja(auctionId, bidAmount);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "listaSubastas"; // Redirige a la vista que muestra las subastas
        }
        return "redirect:/listaSubastas";
    }

    /**
     * Procesa la solicitud para eliminar una subasta.
     * 
     * @param id ID de la subasta a eliminar.
     * @return redirecciona a la lista de subastas después de eliminar la subasta.
     */
    @GetMapping("/borrarSubasta/{id}")
    public String eliminarSubasta(@PathVariable Long id) {
        subastaServicio.eliminarSubasta(id);
        return "redirect:/listaSubastas";
    }

    /**
     * Muestra el formulario para editar una subasta existente.
     * 
     * @param id ID de la subasta a editar.
     * @param model objeto Model que se utiliza para pasar datos a la vista.
     * @return el nombre de la vista del formulario de edición de subasta.
     */
    @GetMapping("/editarSubasta/{id}")
    public String editarSubasta(@PathVariable("id") Long id, Model model) {
        Subasta subasta = subastaServicio.obtenerSubastaPorId(id);
        model.addAttribute("subasta", subasta);
        return "editarSubasta";
    }

    /**
     * Procesa la solicitud para actualizar una subasta existente.
     * 
     * @param id ID de la subasta a actualizar.
     * @param subastaActualizada objeto Subasta con los nuevos datos de la subasta.
     * @return redirecciona a la lista de subastas después de actualizar la subasta.
     */
    @PostMapping("/actualizarSubasta/{id}")
    public String actualizarSubasta(@PathVariable("id") Long id,
            @ModelAttribute("subasta") Subasta subastaActualizada) {
        Subasta subastaExistente = subastaServicio.obtenerSubastaPorId(id);
        if (subastaExistente != null) {
            subastaExistente.setArticulo(subastaActualizada.getArticulo());
            subastaExistente.setDescripcion(subastaActualizada.getDescripcion());
            subastaExistente.setPrecioInicial(subastaActualizada.getPrecioInicial());
            subastaExistente.setFechaInicio(subastaActualizada.getFechaInicio());
            subastaExistente.setFechaFin(subastaActualizada.getFechaFin());
            subastaServicio.actualizarSubasta(subastaExistente);
        }
        return "redirect:/listaSubastas"; // Redirigir a la lista de subastas después de actualizar
    }
}
