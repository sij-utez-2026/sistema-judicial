package mx.edu.utez.sij.copias.repository.memoria;

import mx.edu.utez.sij.copias.model.ColorArea;
import mx.edu.utez.sij.copias.repository.ColorAreaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** TEMPORAL: ver RepositorioMemoria. */
@Repository
public class ColorAreaRepositoryMemoria extends RepositorioMemoria<ColorArea> implements ColorAreaRepository {

    @Override
    protected Long idDe(ColorArea registro) {
        return registro.getId();
    }

    @Override
    protected void asignarId(ColorArea registro, Long id) {
        registro.setId(id);
    }

    @Override
    public Optional<ColorArea> findByVariable(String variable) {
        return filtrar(c -> c.getVariable().equals(variable)).stream().findFirst();
    }
}
