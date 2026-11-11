package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.Tarifa;

import java.time.LocalDate;
import java.util.Optional;

/** Tarifas. Para JPA: extends JpaRepository<Tarifa, Long> y borrar findById/save/count. */
public interface TarifaRepository {

    Optional<Tarifa> findById(Long id);

    Tarifa save(Tarifa tarifa);

    long count();

    /** Tarifa vigente en la fecha: la más reciente que ya empezó. */
    Optional<Tarifa> findFirstByVigenteDesdeLessThanEqualOrderByVigenteDesdeDesc(LocalDate fecha);
}
