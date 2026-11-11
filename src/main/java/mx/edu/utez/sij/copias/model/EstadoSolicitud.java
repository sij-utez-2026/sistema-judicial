package mx.edu.utez.sij.copias.model;

/**
 * Estados de una solicitud de copia certificada (docs/modulos/copias-certificadas.md, sección 5).
 * Los textos son los del DFR 2.3, más "Lista para recoger" [Propuesta del equipo].
 * El color es el de la etiqueta <n:estado> (verde, ambar, gris, rojo o azul).
 */
public enum EstadoSolicitud {

    EN_ESPERA_VALIDACION("En espera de validación", "ambar"),
    APROBADO("Aprobado", "azul"),
    LISTO_PARA_PAGO("Listo para pago", "azul"),
    CANCELADO("Cancelado", "rojo"),
    PAGADO_EN_PREPARACION("Pagado y en preparación", "ambar"),
    LISTO_PARA_RECOGER("Lista para recoger", "verde"),
    ENTREGADO("Entregado", "gris");

    private final String texto;
    private final String color;

    EstadoSolicitud(String texto, String color) {
        this.texto = texto;
        this.color = color;
    }

    public String getTexto() {
        return texto;
    }

    public String getColor() {
        return color;
    }

    /** Se puede pagar: pública recién capturada o privada con visto bueno (RN-01). */
    public boolean sePuedePagar() {
        return this == LISTO_PARA_PAGO || this == APROBADO;
    }

    /** Ya se pagó y falta entregar la copia física. */
    public boolean pendienteDeEntrega() {
        return this == PAGADO_EN_PREPARACION || this == LISTO_PARA_RECOGER;
    }
}
