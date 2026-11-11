package mx.edu.utez.sij.copias.controller.admin;

import jakarta.validation.Valid;
import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.dto.PersonalForm;
import mx.edu.utez.sij.copias.service.PersonalCopiasService;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
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
 * Trabajadores del área (RF 6.1, COPIAS_ADMIN): alta, desactivar y reactivar a COPIAS_PERSONAL.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_ADMIN')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/admin/personal")
public class TrabajadoresController {

    private final PersonalCopiasService personalService;
    private final AccesoTemporal acceso;

    public TrabajadoresController(PersonalCopiasService personalService, AccesoTemporal acceso) {
        this.personalService = personalService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloAdministrador() {
        acceso.exigirRol(RolesCopias.ADMIN);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("personalForm", new PersonalForm());
        return mostrar(model);
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("personalForm") PersonalForm form,
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
        return mostrar(model);
    }

    @PostMapping("/{id}/estado")
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

    private String mostrar(Model model) {
        model.addAttribute("trabajadores", personalService.trabajadores());
        return "copias/admin/personal";
    }
}
