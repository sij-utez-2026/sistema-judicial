package mx.edu.utez.sij.peritajes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del módulo (/peritajes). Equipo 3: cámbienla por el inicio según el rol que pide su DFR.
 * Vista: front-end/WEB-INF/jsp/peritajes/inicio.jsp
 *
 * TODO: agregar @PreAuthorize con los roles del módulo cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioPeritajesController {

    @GetMapping("/peritajes")
    public String inicio() {
        return "peritajes/inicio";
    }
}
