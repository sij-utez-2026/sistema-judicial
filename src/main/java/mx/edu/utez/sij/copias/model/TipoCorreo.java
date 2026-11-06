package mx.edu.utez.sij.copias.model;

/** Correos que envía el módulo al solicitante. */
public enum TipoCorreo {

    COMPROBANTE("Comprobante de pago"),
    LISTA_PARA_RECOGER("Aviso: copia lista para recoger");

    private final String texto;

    TipoCorreo(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
