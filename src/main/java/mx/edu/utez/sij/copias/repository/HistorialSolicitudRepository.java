package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.HistorialSolicitud;

import java.util.List;

/** Historial de estados. Para JPA: extends JpaRepository<HistorialSolicitud, Long> y borrar save. */
public interface HistorialSolicitudRepository {

    HistorialSolicitud save(HistorialSolicitud historial);

    /** Del primer cambio al último. */
    List<HistorialSolicitud> findBySolicitudIdOrderByFechaAsc(Long solicitudId);
}
