package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a las solicitudes. Los nombres siguen las reglas de Spring Data JPA.
 *
 * Hoy la implementa memoria/SolicitudCopiaRepositoryMemoria. Para pasar a la base de datos:
 * 1. Cambiar la declaración por: public interface SolicitudCopiaRepository extends JpaRepository<SolicitudCopia, Long>
 * 2. Borrar findById, save, findAll y count de esta interfaz (ya vienen en JpaRepository).
 * 3. Al método con comentario "JPA:" ponerle la @Query que indica y @Param en sus parámetros.
 * 4. Borrar la clase en memoria.
 * Los services y controllers no cambian.
 */
public interface SolicitudCopiaRepository {

    Optional<SolicitudCopia> findById(Long id);

    SolicitudCopia save(SolicitudCopia solicitud);

    List<SolicitudCopia> findAll();

    long count();

    boolean existsByFolio(String folio);

    Optional<SolicitudCopia> findByFolio(String folio);

    long countByEstado(EstadoSolicitud estado);

    /** Bandejas de validación y entregas: las más antiguas primero. */
    List<SolicitudCopia> findByEstadoInOrderByFechaSolicitudAsc(Collection<EstadoSolicitud> estados);

    /**
     * Registro de solicitudes. Cada filtro es opcional (null = no filtra). Las más recientes primero.
     * JPA:
     * {@code @Query("SELECT s FROM SolicitudCopia s WHERE "
     *     + "(:texto IS NULL OR LOWER(s.folio) LIKE LOWER(CONCAT('%', :texto, '%')) "
     *     + "   OR LOWER(s.numeroExpediente) LIKE LOWER(CONCAT('%', :texto, '%')) "
     *     + "   OR LOWER(s.solicitanteNombre) LIKE LOWER(CONCAT('%', :texto, '%'))) "
     *     + "AND (:estado IS NULL OR s.estado = :estado) "
     *     + "ORDER BY s.fechaSolicitud DESC")}
     */
    Page<SolicitudCopia> buscar(String texto, EstadoSolicitud estado, Pageable pageable);
}
