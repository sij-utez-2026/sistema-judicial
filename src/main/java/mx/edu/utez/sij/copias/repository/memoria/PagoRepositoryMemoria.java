package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.repository.PagoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class PagoRepositoryMemoria extends RepositorioMemoria<Pago> implements PagoRepository {

    @Override
    protected Long idDe(Pago registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(Pago registro, Long id) {
        registro.setId(id);
    }

    @Override
    public Optional<Pago> findBySolicitudId(Long solicitudId) {
        return filtrar(p -> p.getSolicitudId().equals(solicitudId)).stream().findFirst();
    }

    @Override
    public boolean existsBySolicitudId(Long solicitudId) {
        return findBySolicitudId(solicitudId).isPresent();
    }

    @Override
    public boolean existsByFolioPago(String folioPago) {
        return !filtrar(p -> p.getFolioPago().equalsIgnoreCase(folioPago)).isEmpty();
    }
}
