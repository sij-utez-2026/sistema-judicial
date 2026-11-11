package mx.edu.utez.sij.copias.temporal;

import mx.edu.utez.sij.copias.RolesCopias;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * TEMPORAL: usuario en sesión mientras no existe el inicio de sesión del núcleo (SesionService + Spring Security).
 *
 * Para poder probar las dos vistas, el rol se guarda en la sesión del navegador y se cambia con el
 * "Modo de prueba" del menú (ModoPruebaController). No hay usuario ni contraseña.
 * Si no se ha elegido nada (o no hay petición web, como en las pruebas), el rol es COPIAS_PERSONAL.
 *
 * Cuando exista el núcleo: usar sesionService.usuarioId() y borrar esta clase, ModoPruebaController
 * y AccesoTemporal (cambiar sus llamadas por @PreAuthorize).
 */
@Service
public class SesionTemporal {

    private static final String ATRIBUTO_ROL = "copias.rolTemporal";

    public Long usuarioId() {
        return 1L;
    }

    public String usuarioNombre() {
        return esAdmin() ? "Administrador de prueba" : "Trabajador de prueba";
    }

    public String rol() {
        RequestAttributes peticion = RequestContextHolder.getRequestAttributes();
        if (peticion == null) {
            return RolesCopias.PERSONAL;
        }
        Object rol = peticion.getAttribute(ATRIBUTO_ROL, RequestAttributes.SCOPE_SESSION);
        return rol == null ? RolesCopias.PERSONAL : rol.toString();
    }

    public boolean esAdmin() {
        return RolesCopias.ADMIN.equals(rol());
    }

    /** Solo acepta los dos roles del módulo. */
    public void cambiarRol(String rol) {
        if (!RolesCopias.PERSONAL.equals(rol) && !RolesCopias.ADMIN.equals(rol)) {
            throw new ReglaNegocioException("Rol no válido");
        }
        RequestAttributes peticion = RequestContextHolder.currentRequestAttributes();
        peticion.setAttribute(ATRIBUTO_ROL, rol, RequestAttributes.SCOPE_SESSION);
    }
}
