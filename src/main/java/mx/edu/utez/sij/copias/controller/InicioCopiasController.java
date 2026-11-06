package mx.edu.utez.sij.copias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del módulo (/copias). Equipo 5: cámbienla por el inicio según el rol que pide su DFR.
 * Vista: front-end/WEB-INF/jsp/copias/inicio.jsp
 *
 * TODO: agregar @PreAuthorize con los roles del módulo cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioCopiasController {

    @GetMapping("/copias")
    public String inicio() {
        return "copias/inicio";
    }
}
