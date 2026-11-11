package mx.edu.utez.sij.copias.temporal;

import mx.edu.utez.sij.copias.RolesCopias;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * TEMPORAL: cambia entre la vista del trabajador y la del administrador (selector "Modo de prueba" del menú).
 * Se borra cuando exista el inicio de sesión del núcleo: el rol saldrá de la cuenta con la que se entra.
 */
@Controller
public class ModoPruebaController {

    private final SesionTemporal sesion;

    public ModoPruebaController(SesionTemporal sesion) {
        this.sesion = sesion;
    }

    @PostMapping("/copias/modo-prueba")
    public String cambiar(@RequestParam String rol, RedirectAttributes flash) {
        try {
            sesion.cambiarRol(rol);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/copias";
        }
        flash.addFlashAttribute("exito", "Modo de prueba: " + (sesion.esAdmin() ? "Administrador" : "Trabajador"));
        return RolesCopias.ADMIN.equals(rol) ? "redirect:/copias/admin" : "redirect:/copias";
    }
}
