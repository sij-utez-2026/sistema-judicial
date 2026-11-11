package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.Tarifa;
import mx.edu.utez.sij.copias.repository.TarifaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class TarifaRepositoryMemoria extends RepositorioMemoria<Tarifa> implements TarifaRepository {

    @Override
    protected Long idDe(Tarifa registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(Tarifa registro, Long id) {
        registro.setId(id);
    }

    @Override
    public Optional<Tarifa> findFirstByVigenteDesdeLessThanEqualOrderByVigenteDesdeDesc(LocalDate fecha) {
        return filtrar(t -> !t.getVigenteDesde().isAfter(fecha)).stream()
                .max(Comparator.comparing(Tarifa::getVigenteDesde));
    }
}
