package mx.edu.utez.sij.peritajes.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Datos comunes para todas las vistas de Peritajes (solo los controllers de este paquete).
 * TEMPORAL: cuando exista el layout del núcleo, la fecha y el usuario en sesión vendrán de ahí.
 */
@ControllerAdvice(basePackageClasses = PeritajesVistasAdvice.class)
public class PeritajesVistasAdvice {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm 'h'", Locale.forLanguageTag("es-MX"));

    // Fecha y hora del encabezado: "19 oct 2026 · 10:15 h"
    @ModelAttribute("fechaHoraActual")
    public String fechaHoraActual() {
        return LocalDateTime.now(ZoneId.of("America/Mexico_City")).format(FORMATO_FECHA).replace(".", "");
    }
}
