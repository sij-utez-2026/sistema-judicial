package mx.edu.utez.sij.peritajes.service;

/**
 * TEMPORAL: mientras no existe nucleo/contratos/ReglaNegocioException (docs/CONTRATOS.md, sección 1).
 * Cuando exista, se borra esta clase y se cambian los import.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
