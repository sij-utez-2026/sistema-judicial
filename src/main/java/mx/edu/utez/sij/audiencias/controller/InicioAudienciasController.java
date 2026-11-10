package mx.edu.utez.sij.audiencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del módulo (/audiencias). Equipo 2: cámbienla por el inicio según el rol que pide su DFR.
 * Vista: front-end/WEB-INF/jsp/audiencias/inicio.jsp
 *
 * TODO: agregar @PreAuthorize con los roles del módulo cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioAudienciasController {

    @GetMapping("/audiencias")
    public String inicio() {
        return "audiencias/inicio";
    }
}
