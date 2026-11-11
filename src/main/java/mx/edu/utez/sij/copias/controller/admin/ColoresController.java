package mx.edu.utez.sij.copias.controller.admin;

import jakarta.validation.Valid;
import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.dto.ColoresForm;
import mx.edu.utez.sij.copias.service.ColoresCopiasService;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Colores de las pantallas de Copias (COPIAS_ADMIN). Solo cambian este módulo.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_ADMIN')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/admin/colores")
public class ColoresController {

    private final ColoresCopiasService coloresService;
    private final AccesoTemporal acceso;

    public ColoresController(ColoresCopiasService coloresService, AccesoTemporal acceso) {
        this.coloresService = coloresService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloAdministrador() {
        acceso.exigirRol(RolesCopias.ADMIN);
    }

    @GetMapping
    public String editar(Model model) {
        model.addAttribute("coloresForm", coloresService.formulario());
        return mostrar(model);
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("coloresForm") ColoresForm form,
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
        return mostrar(model);
    }

    @PostMapping("/restablecer")
    public String restablecer(RedirectAttributes flash) {
        coloresService.restablecer();
        flash.addFlashAttribute("exito", "Colores restablecidos");
        flash.addFlashAttribute("exitoDetalle", "Se volvió a la paleta institucional.");
        return "redirect:/copias/admin/colores";
    }

    private String mostrar(Model model) {
        model.addAttribute("coloresIniciales", coloresService.formularioInicial());
        return "copias/admin/colores";
    }
}
