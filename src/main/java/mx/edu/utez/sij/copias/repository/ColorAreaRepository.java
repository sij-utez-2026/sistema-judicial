package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.ColorArea;

import java.util.List;
import java.util.Optional;

/** Colores del área. Para JPA: extends JpaRepository<ColorArea, Long> y borrar save/findAll. */
public interface ColorAreaRepository {

    ColorArea save(ColorArea color);

    List<ColorArea> findAll();

    Optional<ColorArea> findByVariable(String variable);
}
