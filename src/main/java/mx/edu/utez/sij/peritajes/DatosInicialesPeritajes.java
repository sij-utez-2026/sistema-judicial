package mx.edu.utez.sij.peritajes;

import mx.edu.utez.sij.peritajes.dto.PeritoForm;
import mx.edu.utez.sij.peritajes.model.Perito;
import mx.edu.utez.sij.peritajes.service.PeritoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Datos de prueba del módulo (docs/ARQUITECTURA.md, sección 11).
 * Solo se insertan si no hay peritos. Los nombres son ficticios (los de los bocetos de Figma) y los
 * correos usan example.com, un dominio reservado para pruebas, para no escribirle a nadie real.
 *
 * @Order: primero el núcleo y Oficialía, después los demás módulos.
 * Materias de prueba: 1 Civil, 2 Familiar, 3 Mercantil, 4 Penal (CatalogoMateriasTemporal).
 */
@Component
@Order(30)
public class DatosInicialesPeritajes implements CommandLineRunner {

    private final PeritoService peritoService;

    public DatosInicialesPeritajes(PeritoService peritoService) {
        this.peritoService = peritoService;
    }

    @Override
    public void run(String... args) {
        if (peritoService.totalRegistrados() > 0) {
            return;
        }

        registrar("PER-0048", "Elena", "Salgado", "Ruiz", "7775550148",
                List.of("Valuación inmobiliaria"), Set.of(1L, 2L), true);
        registrar("PER-0051", "Daniel", "Ríos", "Vega", "7775550151",
                List.of("Valuación inmobiliaria", "Arquitectura"), Set.of(1L, 3L), true);
        registrar("PER-0032", "Lucía", "Torres", "Mora", "7775550132",
                List.of("Criminalística", "Balística"), Set.of(1L, 4L), true);
        registrar("PER-0041", "Óscar", "Medina", "Sol", "7775550141",
                List.of("Contabilidad"), Set.of(3L), true);
        registrar("PER-0056", "Marina", "León", "Soto", "7775550186",
                List.of("Psicología", "Psicología forense"), Set.of(2L), true);
        registrar("PER-0027", "Arturo", "Luna", "Gil", "7775550127",
                List.of("Documentoscopía", "Grafología"), Set.of(1L, 4L), false);
        registrar("PER-0063", "Patricia", "Ramos", null, "7775550163",
                List.of("Medicina legal"), Set.of(2L, 4L), true);
        registrar("PER-0070", "Jorge", "Castillo", "Peña", "7775550170",
                List.of("Contabilidad", "Auditoría financiera"), Set.of(1L, 3L), false);
    }

    private void registrar(String numeroRegistro, String nombre, String apellidoPaterno, String apellidoMaterno,
                           String telefono, List<String> especialidades, Set<Long> materias, boolean activo) {
        PeritoForm form = new PeritoForm();
        form.setNumeroRegistro(numeroRegistro);
        form.setNombre(nombre);
        form.setApellidoPaterno(apellidoPaterno);
        form.setApellidoMaterno(apellidoMaterno);
        form.setCorreo(numeroRegistro.toLowerCase() + "@example.com");
        form.setTelefono(telefono);
        form.setEspecialidades(new ArrayList<>(especialidades));
        form.setMateriasIds(materias);

        // Se usa el service para que pasen por las mismas reglas que un registro normal
        Perito perito = peritoService.registrar(form);
        if (!activo) {
            peritoService.cambiarEstado(perito.getId(), false);
        }
    }
}
