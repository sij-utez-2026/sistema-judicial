package mx.edu.utez.sij.peritajes.service;

import mx.edu.utez.sij.peritajes.dto.CargaPerito;
import mx.edu.utez.sij.peritajes.dto.MateriaDto;
import mx.edu.utez.sij.peritajes.dto.PeritajeActivoDto;
import mx.edu.utez.sij.peritajes.dto.PeritoForm;
import mx.edu.utez.sij.peritajes.model.Perito;
import mx.edu.utez.sij.peritajes.repository.PeritoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Padrón de peritos (RF 3.1 – 3.4). Reglas en docs/modulos/peritajes.md, sección 6.7.
 *
 * Pendiente cuando existan la base de datos y el núcleo:
 * - @Transactional en los métodos que guardan (hoy no hay administrador de transacciones),
 * - registrar con usuarioService.crear(nombre, correo, "PERITAJES_PERITO") (contraseña temporal por correo),
 * - baja y reactivación con usuarioService.quitarRol(...) y usuarioService.agregarRol(...),
 * - cada acción en la bitácora con bitacoraService,
 * - materias con CatalogoJudicialApi en lugar de CatalogoMateriasTemporal.
 */
@Service
public class PeritoService {

    // RF 3.2: 25 registros por página y el administrador puede cambiar la cantidad
    public static final int TAMANO_PAGINA = 25;
    public static final List<Integer> TAMANOS_PAGINA = List.of(10, 25, 50, 100);

    private static final ZoneId ZONA = ZoneId.of("America/Mexico_City");

    private final PeritoRepository peritoRepository;
    private final CatalogoMateriasTemporal catalogoMaterias;

    public PeritoService(PeritoRepository peritoRepository, CatalogoMateriasTemporal catalogoMaterias) {
        this.peritoRepository = peritoRepository;
        this.catalogoMaterias = catalogoMaterias;
    }

    /** Catálogo con filtros opcionales (null o vacío = no filtra). */
    public Page<Perito> listar(String texto, String especialidad, Long materiaId, Boolean activo,
                               int pagina, int tamano) {
        // Si piden un tamaño que no está en la lista, se usa el de 25
        int tamanoValido = TAMANOS_PAGINA.contains(tamano) ? tamano : TAMANO_PAGINA;
        return peritoRepository.buscar(vacioANull(texto), vacioANull(especialidad), materiaId, activo,
                PageRequest.of(Math.max(pagina, 0), tamanoValido));
    }

    public Optional<Perito> buscar(Long id) {
        return peritoRepository.findById(id);
    }

    /** RF 3.1 Registro. El perito siempre empieza Activo. */
    public Perito registrar(PeritoForm form) {
        String numeroRegistro = form.getNumeroRegistro().trim().toUpperCase();
        String correo = form.getCorreo().trim().toLowerCase();

        // RF 3.1: si ya existe (por identificador o correo) se avisa y no se registra
        if (peritoRepository.existsByNumeroRegistroIgnoreCase(numeroRegistro)) {
            throw new ReglaNegocioException("El perito ya existe: hay un perito registrado con el número " + numeroRegistro);
        }
        if (peritoRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El perito ya existe: hay un perito registrado con el correo " + correo);
        }
        validarMaterias(form);

        Perito perito = new Perito();
        perito.setNumeroRegistro(numeroRegistro);
        perito.setCorreo(correo);
        copiarDatosEditables(form, perito);
        perito.setActivo(true);
        perito.setCreadoEn(LocalDateTime.now(ZONA));
        return peritoRepository.save(perito);
    }

    /**
     * RF 3.3 Edición. El correo y el número de registro no se cambian aunque lleguen en el formulario.
     * Cambiar las materias no afecta los casos ya asignados (6.7).
     */
    public Perito actualizar(Long id, PeritoForm form) {
        Perito perito = obtener(id);
        validarMaterias(form);

        copiarDatosEditables(form, perito);
        perito.setActualizadoEn(LocalDateTime.now(ZONA));
        return peritoRepository.save(perito);
    }

    /**
     * RF 3.4 Baja o reactivación (no se borra el perito).
     * No se puede desactivar si tiene peritajes Asignados o En Proceso.
     */
    public Perito cambiarEstado(Long id, boolean activo) {
        Perito perito = obtener(id);

        if (!activo && perito.isActivo()) {
            CargaPerito carga = carga(id);
            if (carga.total() > 0) {
                String folios = peritajesActivos(id).stream()
                        .map(PeritajeActivoDto::folio)
                        .collect(Collectors.joining(", "));
                // RF 3.4 y regla de negocio 8 del DFR
                throw new ReglaNegocioException("El perito tiene " + carga.total() + " peritajes activos ("
                        + folios + "). Primero deben reasignarse para poder darlo de baja");
            }
        }

        perito.setActivo(activo);
        perito.setActualizadoEn(LocalDateTime.now(ZONA));
        return peritoRepository.save(perito);
    }

    /**
     * Carga activa (6.1).
     * TODO equipo 3: contar con PeritajeRepository cuando existan los peritajes.
     */
    public CargaPerito carga(Long peritoId) {
        return new CargaPerito(0, 0);
    }

    /** TODO equipo 3: folio y estado de los peritajes Asignados y En Proceso del perito. */
    public List<PeritajeActivoDto> peritajesActivos(Long peritoId) {
        return List.of();
    }

    public long totalRegistrados() {
        return peritoRepository.count();
    }

    public long totalActivos() {
        return peritoRepository.countByActivo(true);
    }

    public long totalInactivos() {
        return peritoRepository.countByActivo(false);
    }

    /** RF 3.2: total de peritos por materia, por ejemplo {Civil=3, Familiar=2, ...}. */
    public Map<String, Long> totalesPorMateria() {
        Map<String, Long> totales = new LinkedHashMap<>();
        for (MateriaDto materia : catalogoMaterias.materiasActivas()) {
            totales.put(materia.nombre(), peritoRepository.contarPorMateria(materia.id()));
        }
        return totales;
    }

    public List<String> especialidadesRegistradas() {
        return peritoRepository.especialidadesRegistradas();
    }

    public List<MateriaDto> materiasActivas() {
        return catalogoMaterias.materiasActivas();
    }

    /** Nombres de las materias del perito para mostrar: "Civil · Familiar". */
    public String nombresMaterias(Perito perito) {
        return perito.getMateriasIds().stream()
                .map(catalogoMaterias::materia)
                .flatMap(Optional::stream)
                .map(MateriaDto::nombre)
                .collect(Collectors.joining(" · "));
    }

    /** Llena el formulario de edición con los datos actuales. */
    public PeritoForm formularioDe(Perito perito) {
        PeritoForm form = new PeritoForm();
        form.setNumeroRegistro(perito.getNumeroRegistro());
        form.setNombre(perito.getNombre());
        form.setApellidoPaterno(perito.getApellidoPaterno());
        form.setApellidoMaterno(perito.getApellidoMaterno());
        form.setCorreo(perito.getCorreo());
        form.setTelefono(perito.getTelefonoFormateado());
        form.setEspecialidades(new ArrayList<>(perito.getEspecialidades()));
        form.setMateriasIds(new LinkedHashSet<>(perito.getMateriasIds()));
        return form;
    }

    private Perito obtener(Long id) {
        return buscar(id).orElseThrow(() -> new ReglaNegocioException("El perito no existe"));
    }

    // Solo los datos que se pueden editar (no correo ni número de registro)
    private void copiarDatosEditables(PeritoForm form, Perito perito) {
        perito.setNombre(form.getNombre().trim());
        perito.setApellidoPaterno(form.getApellidoPaterno().trim());
        perito.setApellidoMaterno(vacioANull(form.getApellidoMaterno()));
        perito.setTelefono(form.getTelefono().replaceAll("\\s", ""));
        perito.setEspecialidades(limpiarEspecialidades(form.getEspecialidades()));
        perito.setMateriasIds(new LinkedHashSet<>(form.getMateriasIds()));
    }

    /**
     * Quita espacios, renglones vacíos y repetidas (sin importar mayúsculas), conservando el orden.
     * RF 3.1 y 3.3: debe quedar al menos una.
     */
    private Set<String> limpiarEspecialidades(List<String> capturadas) {
        Set<String> limpias = new LinkedHashSet<>();
        Set<String> vistas = new LinkedHashSet<>();
        for (String especialidad : capturadas) {
            if (especialidad != null && !especialidad.isBlank()) {
                String texto = especialidad.trim().replaceAll("\\s+", " ");
                if (texto.length() > 100) {
                    throw new ReglaNegocioException("Cada especialidad debe tener máximo 100 caracteres");
                }
                if (vistas.add(texto.toLowerCase())) {
                    limpias.add(texto);
                }
            }
        }
        if (limpias.isEmpty()) {
            throw new ReglaNegocioException("La especialidad es un campo obligatorio: capture al menos una");
        }
        return limpias;
    }

    // Evita ids de materia que no están en el catálogo (por ejemplo, alterando el formulario)
    private void validarMaterias(PeritoForm form) {
        boolean todasValidas = form.getMateriasIds().stream()
                .allMatch(materiaId -> catalogoMaterias.materia(materiaId).isPresent());
        if (form.getMateriasIds().isEmpty() || !todasValidas) {
            throw new ReglaNegocioException("Seleccione al menos una materia válida para la asignación");
        }
    }

    private String vacioANull(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
