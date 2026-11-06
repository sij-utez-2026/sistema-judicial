package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.service.ColoresCopiasService;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * Datos comunes para todas las pantallas de Copias (solo los controllers de este paquete).
 * coloresCopias: los colores del área que pone tags/copias/estilos.tag.
 * esAdminCopias: qué menú mostrar (trabajador o administrador).
 */
@ControllerAdvice(basePackageClasses = CopiasVistasAdvice.class)
public class CopiasVistasAdvice {

    private final ColoresCopiasService coloresService;
    private final SesionTemporal sesion;

    public CopiasVistasAdvice(ColoresCopiasService coloresService, SesionTemporal sesion) {
        this.coloresService = coloresService;
        this.sesion = sesion;
    }

    @ModelAttribute("coloresCopias")
    public Map<String, String> coloresCopias() {
        return coloresService.actuales();
    }

    /**
     * Rol actual, para mostrar en menu.jsp solo las opciones de ese rol.
     * TODO: con Spring Security, menu.jsp usa <sec:authorize> y esto se borra.
     */
    @ModelAttribute("esAdminCopias")
    public boolean esAdminCopias() {
        return sesion.esAdmin();
    }
}
