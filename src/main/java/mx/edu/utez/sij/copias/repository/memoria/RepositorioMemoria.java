package mx.edu.utez.sij.copias.repository.memoria;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * TEMPORAL: base de los repositorios en memoria mientras no hay base de datos (los datos se pierden al reiniciar).
 * Imita lo que JpaRepository ya trae (findById, save, findAll, count).
 * Se borra junto con todo el paquete "memoria" cuando los repositorios extiendan JpaRepository.
 */
public abstract class RepositorioMemoria<T> {

    private final Map<Long, T> registros = new LinkedHashMap<>();
    private long siguienteId = 1;

    protected abstract Long idDe(T registro);

    protected abstract void asignarId(T registro, Long id);

    public synchronized Optional<T> findById(Long id) {
        return Optional.ofNullable(registros.get(id));
    }

    public synchronized T save(T registro) {
        if (idDe(registro) == null) {
            asignarId(registro, siguienteId++);
        }
        registros.put(idDe(registro), registro);
        return registro;
    }

    public synchronized List<T> findAll() {
        return new ArrayList<>(registros.values());
    }

    public synchronized long count() {
        return registros.size();
    }

    /** Registros que cumplen la condición, en el orden en que se guardaron. */
    protected synchronized List<T> filtrar(Predicate<T> condicion) {
        return registros.values().stream().filter(condicion).toList();
    }
}
