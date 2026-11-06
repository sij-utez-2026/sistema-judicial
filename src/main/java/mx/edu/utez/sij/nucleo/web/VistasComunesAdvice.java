package mx.edu.utez.sij.nucleo.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Datos que el layout (front-end/WEB-INF/tags/layout.tag) necesita en todas las pantallas.
 * Los módulos no tienen que agregarlos en sus controllers.
 */
@ControllerAdvice
public class VistasComunesAdvice {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm 'h'", Locale.forLanguageTag("es-MX"));

    /** Fecha y hora del encabezado: "4 nov 2026 · 10:15 h". */
    @ModelAttribute("fechaHoraActual")
    public String fechaHoraActual() {
        return LocalDateTime.now(ZoneId.of("America/Mexico_City")).format(FORMATO_FECHA).replace(".", "");
    }

    /** Módulos del sistema: nombre del módulo actual en el layout y tarjetas de la página de inicio. */
    @ModelAttribute("modulosSistema")
    public List<ModuloSistema> modulosSistema() {
        // TODO núcleo: con Spring Security, devolver solo los módulos de los roles del usuario
        return List.of(ModuloSistema.values());
    }

    /** Usuario que se muestra en el menú lateral. */
    @ModelAttribute("usuarioActual")
    public UsuarioVista usuarioActual() {
        // TODO núcleo: tomarlo de SesionService cuando exista el inicio de sesión
        return new UsuarioVista("Usuario de prueba", "Sin sesión (pendiente del núcleo)");
    }

    /**
     * Nombre y rol del usuario en sesión, como se muestran en el menú lateral.
     * Es una clase con getters (y no un record) porque en una JSP ${usuarioActual.iniciales}
     * solo funciona con métodos getX(); de un record solo se leen sus campos.
     */
    public static class UsuarioVista {

        private final String nombre;
        private final String rol;

        public UsuarioVista(String nombre, String rol) {
            this.nombre = nombre;
            this.rol = rol;
        }

        public String getNombre() {
            return nombre;
        }

        public String getRol() {
            return rol;
        }

        /** "Usuario de prueba" → "UP". */
        public String getIniciales() {
            StringBuilder iniciales = new StringBuilder();
            for (String palabra : nombre.trim().split("\\s+")) {
                if (!palabra.isEmpty() && iniciales.length() < 2) {
                    iniciales.append(Character.toUpperCase(palabra.charAt(0)));
                }
            }
            return iniciales.toString();
        }
    }
}
