package mx.edu.utez.sij.peritajes.repository;

import mx.edu.utez.sij.peritajes.model.Perito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a los datos de los peritos. Los nombres siguen las reglas de Spring Data JPA.
 *
 * Hoy la implementa PeritoRepositoryMemoria. Para pasar a la base de datos:
 * 1. Cambiar la declaración por: public interface PeritoRepository extends JpaRepository<Perito, Long>
 * 2. Borrar findById, save y count de esta interfaz (ya vienen en JpaRepository).
 * 3. Poner a cada método con comentario "JPA:" la anotación @Query que indica, y @Param en sus parámetros.
 * 4. Borrar PeritoRepositoryMemoria.
 * El service y el controller no cambian.
 */
public interface PeritoRepository {

    Optional<Perito> findById(Long id);

    Perito save(Perito perito);

    long count();

    long countByActivo(boolean activo);

    /**
     * Peritos que tienen la materia (RF 3.2: total por materia).
     * JPA: {@code @Query("SELECT COUNT(p) FROM Perito p WHERE :materiaId MEMBER OF p.materiasIds")}
     */
    long contarPorMateria(Long materiaId);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByNumeroRegistroIgnoreCase(String numeroRegistro);

    /**
     * Búsqueda del catálogo. Cada filtro es opcional (null = no filtra).
     * JPA:
     * {@code @Query("SELECT p FROM Perito p WHERE "
     *     + "(:texto IS NULL OR LOWER(CONCAT(p.nombre, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))) LIKE LOWER(CONCAT('%', :texto, '%')) "
     *     + "   OR LOWER(p.numeroRegistro) LIKE LOWER(CONCAT('%', :texto, '%'))) "
     *     + "AND (:especialidad IS NULL OR :especialidad MEMBER OF p.especialidades) "
     *     + "AND (:materiaId IS NULL OR :materiaId MEMBER OF p.materiasIds) "
     *     + "AND (:activo IS NULL OR p.activo = :activo) "
     *     + "ORDER BY p.nombre, p.apellidoPaterno")}
     */
    Page<Perito> buscar(String texto, String especialidad, Long materiaId, Boolean activo, Pageable pageable);

    /**
     * Especialidades distintas ya registradas, para el filtro del catálogo.
     * JPA: {@code @Query("SELECT DISTINCT e FROM Perito p JOIN p.especialidades e ORDER BY e")}
     */
    List<String> especialidadesRegistradas();
}
