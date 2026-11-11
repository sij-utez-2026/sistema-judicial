package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.Entrega;

import java.util.Optional;

/** Entregas. Para JPA: extends JpaRepository<Entrega, Long> y borrar findById/save (ver SolicitudCopiaRepository). */
public interface EntregaRepository {

    Optional<Entrega> findById(Long id);

    Entrega save(Entrega entrega);

    Optional<Entrega> findBySolicitudId(Long solicitudId);
}
