package mx.edu.utez.sij.copias.controller;

import jakarta.validation.Valid;
import mx.edu.utez.sij.copias.dto.ColoresForm;
import mx.edu.utez.sij.copias.dto.PersonalForm;
import mx.edu.utez.sij.copias.service.ColoresCopiasService;
import mx.edu.utez.sij.copias.service.PersonalCopiasService;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administración del área (COPIAS_ADMIN): trabajadores y colores de las pantallas de Copias.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_ADMIN')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/admin")
public class AdminCopiasController {

    private final PersonalCopiasService personalService;
    private final ColoresCopiasService coloresService;

    public AdminCopiasController(PersonalCopiasService personalService, ColoresCopiasService coloresService) {
        this.personalService = personalService;
        this.coloresService = coloresService;
    }

    // ===== Trabajadores =====

    @GetMapping("/personal")
    public String personal(Model model) {
        model.addAttribute("personalForm", new PersonalForm());
        return mostrarPersonal(model);
    }

    @PostMapping("/personal")
    public String registrarPersonal(@Valid @ModelAttribute("personalForm") PersonalForm form,
                                    BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                Usuario usuario = personalService.registrar(form);
                flash.addFlashAttribute("exito", "Trabajador agregado correctamente");
                flash.addFlashAttribute("exitoDetalle", usuario.getNombre() + " · " + usuario.getCorreo()
                        + " ya puede atender solicitudes.");
                return "redirect:/copias/admin/personal";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return mostrarPersonal(model);
    }

    @PostMapping("/personal/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam boolean activo, RedirectAttributes flash) {
        try {
            Usuario usuario = personalService.cambiarEstado(id, activo);
            flash.addFlashAttribute("exito", activo ? "Trabajador reactivado" : "Trabajador desactivado");
            flash.addFlashAttribute("exitoDetalle", usuario.getNombre()
                    + (activo ? " puede volver a entrar al sistema." : " ya no puede entrar al sistema."));
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/copias/admin/personal";
    }

    private String mostrarPersonal(Model model) {
        model.addAttribute("trabajadores", personalService.trabajadores());
        return "copias/admin/personal";
    }

    // ===== Colores =====

    @GetMapping("/colores")
    public String colores(Model model) {
        model.addAttribute("coloresForm", coloresService.formulario());
        return mostrarColores(model);
    }

    @PostMapping("/colores")
    public String guardarColores(@Valid @ModelAttribute("coloresForm") ColoresForm form,
                                 BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                coloresService.guardar(form);
                flash.addFlashAttribute("exito", "Colores actualizados correctamente");
                return "redirect:/copias/admin/colores";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return mostrarColores(model);
    }

    @PostMapping("/colores/restablecer")
    public String restablecerColores(RedirectAttributes flash) {
        coloresService.restablecer();
        flash.addFlashAttribute("exito", "Colores restablecidos");
        flash.addFlashAttribute("exitoDetalle", "Se volvió a la paleta institucional.");
        return "redirect:/copias/admin/colores";
    }

    private String mostrarColores(Model model) {
        model.addAttribute("coloresIniciales", coloresService.formularioInicial());
        return "copias/admin/colores";
    }
}
