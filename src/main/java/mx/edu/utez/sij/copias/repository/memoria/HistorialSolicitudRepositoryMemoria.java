package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.HistorialSolicitud;
import mx.edu.utez.sij.copias.repository.HistorialSolicitudRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class HistorialSolicitudRepositoryMemoria extends RepositorioMemoria<HistorialSolicitud>
        implements HistorialSolicitudRepository {

    @Override
    protected Long idDe(HistorialSolicitud registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(HistorialSolicitud registro, Long id) {
        registro.setId(id);
    }

    @Override
    public List<HistorialSolicitud> findBySolicitudIdOrderByFechaAsc(Long solicitudId) {
        return filtrar(h -> h.getSolicitudId().equals(solicitudId)).stream()
                .sorted(Comparator.comparing(HistorialSolicitud::getFecha).thenComparing(HistorialSolicitud::getId))
                .toList();
    }
}
