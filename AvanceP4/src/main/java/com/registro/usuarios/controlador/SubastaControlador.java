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

@Controller
public class SubastaControlador {

    @Autowired
    private SubastaService subastaServicio;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format");
                }
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value) : "";
            }
        });
    }

    @GetMapping("/crearSubasta")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("subasta", new Subasta());
        return "crearSubasta";
    }

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

    private boolean esObjetoRestringido(String articulo) {
        String[] objetosRestringidos = { "arma", "explosivo", "sustancia psicoactiva" };
        for (String objeto : objetosRestringidos) {
            if (articulo.toLowerCase().contains(objeto)) {
                return true;
            }
        }
        return false;
    }

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

    @GetMapping("/borrarSubasta/{id}")
    public String eliminarSubasta(@PathVariable Long id) {
        subastaServicio.eliminarSubasta(id);
        return "redirect:/listaSubastas";
    }

    // Método para mostrar el formulario de edición
    @GetMapping("/editarSubasta/{id}")
    public String editarSubasta(@PathVariable("id") Long id, Model model) {
        Subasta subasta = subastaServicio.obtenerSubastaPorId(id);
        model.addAttribute("subasta", subasta);
        return "editarSubasta";
    }

    // Método para actualizar la subasta
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
