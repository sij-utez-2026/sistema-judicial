package mx.edu.utez.sij.peritajes.repository;

import mx.edu.utez.sij.peritajes.model.Perito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TEMPORAL: guarda los peritos en memoria mientras no hay base de datos (se pierden al reiniciar).
 * Se borra cuando PeritoRepository extienda JpaRepository (ver los pasos en PeritoRepository).
 */
@Repository
public class PeritoRepositoryMemoria implements PeritoRepository {

    private final Map<Long, Perito> peritos = new LinkedHashMap<>();
    private long siguienteId = 1;

    @Override
    public synchronized Optional<Perito> findById(Long id) {
        return Optional.ofNullable(peritos.get(id));
    }

    @Override
    public synchronized Perito save(Perito perito) {
        if (perito.getId() == null) {
            perito.setId(siguienteId++);
        }
        peritos.put(perito.getId(), perito);
        return perito;
    }

    @Override
    public synchronized long count() {
        return peritos.size();
    }

    @Override
    public synchronized long countByActivo(boolean activo) {
        return peritos.values().stream().filter(p -> p.isActivo() == activo).count();
    }

    @Override
    public synchronized long contarPorMateria(Long materiaId) {
        return peritos.values().stream().filter(p -> p.getMateriasIds().contains(materiaId)).count();
    }

    @Override
    public synchronized boolean existsByCorreoIgnoreCase(String correo) {
        return peritos.values().stream().anyMatch(p -> p.getCorreo().equalsIgnoreCase(correo));
    }

    @Override
    public synchronized boolean existsByNumeroRegistroIgnoreCase(String numeroRegistro) {
        return peritos.values().stream().anyMatch(p -> p.getNumeroRegistro().equalsIgnoreCase(numeroRegistro));
    }

    @Override
    public synchronized Page<Perito> buscar(String texto, String especialidad, Long materiaId, Boolean activo,
                                            Pageable pageable) {
        String t = (texto == null) ? null : texto.toLowerCase();

        List<Perito> filtrados = peritos.values().stream()
                .filter(p -> t == null
                        || p.getNombreCompleto().toLowerCase().contains(t)
                        || p.getNumeroRegistro().toLowerCase().contains(t))
                .filter(p -> especialidad == null || p.getEspecialidades().contains(especialidad))
                .filter(p -> materiaId == null || p.getMateriasIds().contains(materiaId))
                .filter(p -> activo == null || p.isActivo() == activo)
                .sorted(Comparator.comparing(Perito::getNombre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Perito::getApellidoPaterno, String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Recorta la página pedida
        int desde = (int) Math.min(pageable.getOffset(), filtrados.size());
        int hasta = Math.min(desde + pageable.getPageSize(), filtrados.size());
        return new PageImpl<>(filtrados.subList(desde, hasta), pageable, filtrados.size());
    }

    @Override
    public synchronized List<String> especialidadesRegistradas() {
        return peritos.values().stream()
                .flatMap(p -> p.getEspecialidades().stream())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
