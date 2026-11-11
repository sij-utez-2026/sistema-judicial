package mx.edu.utez.sij.copias.temporal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * TEMPORAL: revisa el rol del "Modo de prueba" mientras no hay Spring Security.
 * Si no corresponde, responde 403 y se ve la página de error del sistema (error/403.jsp).
 *
 * Cada controller lo llama en un método @ModelAttribute, que Spring ejecuta antes de cualquiera de sus rutas.
 * Cuando exista Spring Security, ese método se borra y la clase del controller lleva
 * @PreAuthorize("hasRole('...')") con el mismo rol.
 */
@Component
public class AccesoTemporal {

    private final SesionTemporal sesion;

    public AccesoTemporal(SesionTemporal sesion) {
        this.sesion = sesion;
    }

    public void exigirRol(String rol) {
        if (!rol.equals(sesion.rol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para ver esta página");
        }
    }
}
