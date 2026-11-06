package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.dto.PersonalForm;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Trabajadores del área: COPIAS_ADMIN da de alta, desactiva y reactiva a COPIAS_PERSONAL
 * (como hacen los administradores de los otros módulos, ARQUITECTURA 7.3).
 * Las cuentas son del núcleo: Copias no tiene tabla de usuarios propia.
 *
 * TODO núcleo: cambiar UsuarioServiceTemporal por UsuarioService. Al dar de baja se le quita el rol
 * (usuarioService.quitarRol) y el núcleo desactiva la cuenta si ya no le quedan roles.
 */
@Service
public class PersonalCopiasService {

    /** Trabajador: captura, valida, cobra y entrega. */
    public static final String ROL_PERSONAL = "COPIAS_PERSONAL";
    /** Administrador del área: trabajadores y colores. */
    public static final String ROL_ADMIN = "COPIAS_ADMIN";

    private final UsuarioServiceTemporal usuarioService;

    public PersonalCopiasService(UsuarioServiceTemporal usuarioService) {
        this.usuarioService = usuarioService;
    }

    public List<Usuario> trabajadores() {
        return usuarioService.conRol(ROL_PERSONAL);
    }

    public Usuario registrar(PersonalForm form) {
        String nombre = form.getNombre().trim().replaceAll("\\s+", " ");
        String correo = form.getCorreo().trim().toLowerCase(Locale.ROOT);
        return usuarioService.crear(nombre, correo, ROL_PERSONAL);
    }

    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioService.buscar(id)
                .filter(u -> u.getRoles().contains(ROL_PERSONAL))
                .orElseThrow(() -> new ReglaNegocioException("El trabajador no existe"));
        usuarioService.cambiarActivo(id, activo);
        return usuario;
    }
}
