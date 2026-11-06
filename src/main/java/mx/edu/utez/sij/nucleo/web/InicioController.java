package mx.edu.utez.sij.nucleo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de inicio del sistema (/): una tarjeta por módulo.
 * Vista: front-end/WEB-INF/jsp/nucleo/inicio.jsp
 */
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "nucleo/inicio";
    }
}
