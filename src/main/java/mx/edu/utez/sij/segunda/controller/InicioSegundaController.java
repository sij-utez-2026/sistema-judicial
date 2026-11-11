package mx.edu.utez.sij.segunda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del módulo (/segunda). Equipo 4: cámbienla por el inicio según el rol que pide su DFR.
 * Vista: front-end/WEB-INF/jsp/segunda/inicio.jsp
 *
 * TODO: agregar @PreAuthorize con los roles del módulo cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioSegundaController {

    @GetMapping("/segunda")
    public String inicio() {
        return "segunda/inicio";
    }
}
