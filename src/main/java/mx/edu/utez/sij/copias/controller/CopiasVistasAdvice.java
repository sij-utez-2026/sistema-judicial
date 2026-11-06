package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.service.ColoresCopiasService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * Datos comunes para todas las pantallas de Copias (solo los controllers de este paquete).
 * coloresCopias: los colores del área que pone tags/copias/estilos.tag.
 */
@ControllerAdvice(basePackageClasses = CopiasVistasAdvice.class)
public class CopiasVistasAdvice {

    private final ColoresCopiasService coloresService;

    public CopiasVistasAdvice(ColoresCopiasService coloresService) {
        this.coloresService = coloresService;
    }

    @ModelAttribute("coloresCopias")
    public Map<String, String> coloresCopias() {
        return coloresService.actuales();
    }
}
