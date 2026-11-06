package mx.edu.utez.sij.nucleo.web;

/**
 * Los cinco módulos del sistema. Lo usan el layout (nombre en el menú lateral y el encabezado)
 * y la página de inicio (/) para sus tarjetas.
 *
 * El prefijo es el mismo de las URLs, del paquete Java y de la carpeta front-end/WEB-INF/jsp/&lt;prefijo&gt;/.
 */
public enum ModuloSistema {

    OFICIALIA("oficialia", "Oficialía de Partes", "Gestión y Recepción de Demandas", "Oficialía",
            "Recepción de demandas, duplicidad, procedencia y catálogos judiciales.", 1),
    AUDIENCIAS("audiencias", "Audiencias", "Sistema de Gestión y Control de Audiencias", "SGCA",
            "Mesas, abogados, agenda de audiencias, actas y resolución de primera instancia.", 2),
    PERITAJES("peritajes", "Peritajes", "Sistema para Gestión de Peritajes Judiciales", "SGPJ",
            "Catálogo de peritos, asignación por carga de trabajo y dictámenes.", 3),
    SEGUNDA("segunda", "Segunda Instancia", "Sistema de Gestión de Tocas de Segunda Instancia", "SSI",
            "Salas, magistrados, tocas, ponencias y resoluciones.", 4),
    COPIAS("copias", "Copias Certificadas", "Sistema de Gestión de Copias Certificadas", "SGCC",
            "Solicitud, validación, pago simulado y entrega de copias certificadas.", 5);

    private final String prefijo;
    private final String nombre;
    private final String nombreLargo;
    private final String sigla;
    private final String descripcion;
    private final int equipo;

    ModuloSistema(String prefijo, String nombre, String nombreLargo, String sigla, String descripcion, int equipo) {
        this.prefijo = prefijo;
        this.nombre = nombre;
        this.nombreLargo = nombreLargo;
        this.sigla = sigla;
        this.descripcion = descripcion;
        this.equipo = equipo;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreLargo() {
        return nombreLargo;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getEquipo() {
        return equipo;
    }
}
