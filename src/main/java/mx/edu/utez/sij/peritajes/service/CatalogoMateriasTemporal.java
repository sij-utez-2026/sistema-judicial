package mx.edu.utez.sij.peritajes.service;

import mx.edu.utez.sij.peritajes.dto.MateriaDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * TEMPORAL: materias de prueba mientras Oficialía no implementa CatalogoJudicialApi.
 * Son los datos iniciales sugeridos en docs/ARQUITECTURA.md (pendiente 2).
 * Cuando exista el contrato, PeritoService debe usar CatalogoJudicialApi y se borra esta clase.
 */
@Service
public class CatalogoMateriasTemporal {

    private final List<MateriaDto> materias = List.of(
            new MateriaDto(1L, "Civil"),
            new MateriaDto(2L, "Familiar"),
            new MateriaDto(3L, "Mercantil"),
            new MateriaDto(4L, "Penal"));

    public List<MateriaDto> materiasActivas() {
        return materias;
    }

    public Optional<MateriaDto> materia(Long materiaId) {
        return materias.stream().filter(m -> m.id().equals(materiaId)).findFirst();
    }
}
