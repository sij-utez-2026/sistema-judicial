package mx.edu.utez.sij.oficialia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del módulo (/oficialia). Equipo 1: cámbienla por el inicio según el rol que pide su DFR.
 * Vista: front-end/WEB-INF/jsp/oficialia/inicio.jsp
 *
 * TODO: agregar @PreAuthorize con los roles del módulo cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioOficialiaController {

    @GetMapping("/oficialia")
    public String inicio() {
        return "oficialia/inicio";
    }
}
