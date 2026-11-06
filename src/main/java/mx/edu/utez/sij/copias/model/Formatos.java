package mx.edu.utez.sij.copias.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatos de fecha y dinero para mostrar en pantalla y en el comprobante.
 * Las JSP no pueden formatear LocalDateTime (front-end/README.md), por eso las entidades exponen getters "...Texto".
 */
public final class Formatos {

    private static final Locale ES_MX = Locale.forLanguageTag("es-MX");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm 'h'", ES_MX);

    /** Zona horaria de todo el sistema (ARQUITECTURA, regla 12). */
    public static final ZoneId ZONA = ZoneId.of("America/Mexico_City");

    private Formatos() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    /** "6 nov 2026 · 10:15 h" (vacío si no hay fecha). */
    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA).replace(".", "");
    }

    /** "$320.00" */
    public static String dinero(BigDecimal monto) {
        return monto == null ? "" : NumberFormat.getCurrencyInstance(ES_MX).format(monto);
    }
}
