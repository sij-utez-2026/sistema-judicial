package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.Pago;

import java.util.Optional;

/** Pagos simulados. Para JPA: extends JpaRepository<Pago, Long> y borrar findById/save (ver SolicitudCopiaRepository). */
public interface PagoRepository {

    Optional<Pago> findById(Long id);

    Pago save(Pago pago);

    Optional<Pago> findBySolicitudId(Long solicitudId);

    boolean existsBySolicitudId(Long solicitudId);

    boolean existsByFolioPago(String folioPago);
}
