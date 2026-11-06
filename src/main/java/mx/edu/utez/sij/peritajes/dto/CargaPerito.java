package mx.edu.utez.sij.peritajes.dto;

/** Carga activa de un perito: peritajes Asignados + En Proceso (peritajes.md 6.1). */
public record CargaPerito(long asignados, long enProceso) {

    public long total() {
        return asignados + enProceso;
    }
}
