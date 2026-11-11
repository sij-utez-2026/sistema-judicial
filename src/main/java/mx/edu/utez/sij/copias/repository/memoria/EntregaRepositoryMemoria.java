package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.Entrega;
import mx.edu.utez.sij.copias.repository.EntregaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class EntregaRepositoryMemoria extends RepositorioMemoria<Entrega> implements EntregaRepository {

    @Override
    protected Long idDe(Entrega registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(Entrega registro, Long id) {
        registro.setId(id);
    }

    @Override
    public Optional<Entrega> findBySolicitudId(Long solicitudId) {
        return filtrar(e -> e.getSolicitudId().equals(solicitudId)).stream().findFirst();
    }
}
