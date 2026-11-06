package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class SolicitudCopiaRepositoryMemoria extends RepositorioMemoria<SolicitudCopia>
        implements SolicitudCopiaRepository {

    @Override
    protected Long idDe(SolicitudCopia registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(SolicitudCopia registro, Long id) {
        registro.setId(id);
    }

    @Override
    public boolean existsByFolio(String folio) {
        return findByFolio(folio).isPresent();
    }

    @Override
    public Optional<SolicitudCopia> findByFolio(String folio) {
        return filtrar(s -> s.getFolio().equalsIgnoreCase(folio)).stream().findFirst();
    }

    @Override
    public long countByEstado(EstadoSolicitud estado) {
        return filtrar(s -> s.getEstado() == estado).size();
    }

    @Override
    public List<SolicitudCopia> findByEstadoInOrderByFechaSolicitudAsc(Collection<EstadoSolicitud> estados) {
        return filtrar(s -> estados.contains(s.getEstado())).stream()
                .sorted(Comparator.comparing(SolicitudCopia::getFechaSolicitud))
                .toList();
    }

    @Override
    public Page<SolicitudCopia> buscar(String texto, EstadoSolicitud estado, Pageable pageable) {
        String t = texto == null ? null : texto.toLowerCase(Locale.ROOT);
        List<SolicitudCopia> encontradas = filtrar(s -> t == null
                || s.getFolio().toLowerCase(Locale.ROOT).contains(t)
                || s.getNumeroExpediente().toLowerCase(Locale.ROOT).contains(t)
                || s.getSolicitanteNombre().toLowerCase(Locale.ROOT).contains(t))
                .stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .sorted(Comparator.comparing(SolicitudCopia::getFechaSolicitud).reversed())
                .toList();

        // Recorta la página pedida
        int desde = (int) Math.min(pageable.getOffset(), encontradas.size());
        int hasta = Math.min(desde + pageable.getPageSize(), encontradas.size());
        return new PageImpl<>(encontradas.subList(desde, hasta), pageable, encontradas.size());
    }
}
