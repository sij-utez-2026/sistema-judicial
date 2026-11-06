package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.repository.CorreoEnviadoRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class CorreoEnviadoRepositoryMemoria extends RepositorioMemoria<CorreoEnviado>
        implements CorreoEnviadoRepository {

    @Override
    protected Long idDe(CorreoEnviado registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(CorreoEnviado registro, Long id) {
        registro.setId(id);
    }

    @Override
    public List<CorreoEnviado> findBySolicitudIdOrderByEnviadoEnDesc(Long solicitudId) {
        return filtrar(c -> c.getSolicitudId().equals(solicitudId)).stream()
                .sorted(Comparator.comparing(CorreoEnviado::getEnviadoEn).thenComparing(CorreoEnviado::getId).reversed())
                .toList();
    }
}
