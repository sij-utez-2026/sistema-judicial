package mx.edu.utez.sij.copias.controller.admin;

import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.service.ColoresCopiasService;
import mx.edu.utez.sij.copias.service.PersonalCopiasService;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Inicio del administrador del área (/copias/admin): trabajadores y colores.
 * Vista: front-end/WEB-INF/jsp/copias/admin/inicio.jsp
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_ADMIN')") cuando los integradores agreguen Spring Security.
 */
@Controller
public class AdminInicioController {

    private final PersonalCopiasService personalService;
    private final ColoresCopiasService coloresService;
    private final AccesoTemporal acceso;

    public AdminInicioController(PersonalCopiasService personalService, ColoresCopiasService coloresService,
                                 AccesoTemporal acceso) {
        this.personalService = personalService;
        this.coloresService = coloresService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloAdministrador() {
        acceso.exigirRol(RolesCopias.ADMIN);
    }

    @GetMapping("/copias/admin")
    public String inicio(Model model) {
        List<Usuario> trabajadores = personalService.trabajadores();
        long activos = trabajadores.stream().filter(Usuario::isActivo).count();
        model.addAttribute("totalTrabajadores", trabajadores.size());
        model.addAttribute("trabajadoresActivos", activos);
        model.addAttribute("trabajadoresInactivos", trabajadores.size() - activos);
        model.addAttribute("coloresPersonalizados", !coloresService.actuales().equals(coloresService.iniciales()));
        return "copias/admin/inicio";
    }
}
