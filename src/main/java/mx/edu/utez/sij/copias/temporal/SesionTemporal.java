package mx.edu.utez.sij.copias.temporal;

import org.springframework.stereotype.Service;

/**
 * TEMPORAL: usuario en sesión mientras no existe SesionService del núcleo (no hay inicio de sesión).
 * Todas las acciones quedan a nombre del "Usuario de prueba" (id 1).
 * Cuando exista el núcleo se usa sesionService.usuarioId() y se borra esta clase.
 */
@Service
public class SesionTemporal {

    public Long usuarioId() {
        return 1L;
    }

    public String usuarioNombre() {
        return "Usuario de prueba";
    }
}
