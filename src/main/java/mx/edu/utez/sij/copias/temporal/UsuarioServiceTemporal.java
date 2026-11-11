package mx.edu.utez.sij.copias.temporal;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * TEMPORAL: imita UsuarioService del núcleo (core_usuario y core_usuario_rol) en memoria.
 * Copias solo lo usa para que COPIAS_ADMIN dé de alta, desactive y reactive a sus trabajadores.
 * Cuando exista el núcleo se cambian los import y se borra esta clase.
 */
@Service
public class UsuarioServiceTemporal {

    /** Usuario como lo devolverá el núcleo. Clase con getters para leerla desde las JSP. */
    public static class Usuario {
        private final Long id;
        private final String nombre;
        private final String correo;
        private final Set<String> roles = new LinkedHashSet<>();
        private boolean activo = true;

        Usuario(Long id, String nombre, String correo) {
            this.id = id;
            this.nombre = nombre;
            this.correo = correo;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public boolean isActivo() {
            return activo;
        }
    }

    private final Map<Long, Usuario> usuarios = new LinkedHashMap<>();
    private long siguienteId = 1;

    /**
     * Crea la cuenta con el rol. En el núcleo, además genera una contraseña temporal y la manda por correo.
     * Si el correo ya existe, solo le agrega el rol.
     */
    public synchronized Usuario crear(String nombre, String correo, String rol) {
        Optional<Usuario> existente = buscarPorCorreo(correo);
        if (existente.isPresent()) {
            if (existente.get().roles.contains(rol)) {
                throw new ReglaNegocioException("Ya existe un usuario con el correo " + correo);
            }
            existente.get().roles.add(rol);
            existente.get().activo = true;
            return existente.get();
        }
        Usuario usuario = new Usuario(siguienteId++, nombre, correo);
        usuario.roles.add(rol);
        usuarios.put(usuario.id, usuario);
        return usuario;
    }

    public synchronized List<Usuario> conRol(String rol) {
        List<Usuario> lista = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            if (usuario.roles.contains(rol)) {
                lista.add(usuario);
            }
        }
        lista.sort(Comparator.comparing(Usuario::getNombre, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public synchronized Optional<Usuario> buscar(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    public synchronized void cambiarActivo(Long id, boolean activo) {
        Usuario usuario = buscar(id).orElseThrow(() -> new ReglaNegocioException("El usuario no existe"));
        usuario.activo = activo;
    }

    private Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarios.values().stream().filter(u -> u.correo.equalsIgnoreCase(correo)).findFirst();
    }
}
