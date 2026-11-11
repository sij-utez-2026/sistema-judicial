package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.dto.SolicitudForm;
import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.model.Tarifa;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import mx.edu.utez.sij.copias.temporal.ArchivoServiceTemporal;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.ExpedienteDto;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Captura y consulta de solicitudes (RF 2.1, 2.2 y 2.3). El trabajador captura los datos que le da el solicitante:
 * - expediente público → "Listo para pago" (no pide INE),
 * - expediente privado → "En espera de validación" (pide INE; no se puede pagar hasta el visto bueno, RN-01).
 * El tipo no lo elige nadie: lo dice Oficialía con ExpedienteApi.
 */
@Service
public class SolicitudCopiaService {

    public static final String MODULO = "COPIAS";
    // INE: tipos y tamaño de la propuesta del documento del módulo (6.2)
    public static final List<String> TIPOS_INE = List.of("pdf", "jpg", "png");
    public static final int TAMANO_MAXIMO_INE_MB = 5;

    public static final int TAMANO_PAGINA = 10;
    public static final List<Integer> TAMANOS_PAGINA = List.of(10, 25, 50);

    private final SolicitudCopiaRepository solicitudRepository;
    private final ExpedienteApi expedienteApi;
    private final ArchivoServiceTemporal archivoService;
    private final TarifaService tarifaService;
    private final FolioService folioService;
    private final HistorialService historialService;
    private final SesionTemporal sesion;

    public SolicitudCopiaService(SolicitudCopiaRepository solicitudRepository, ExpedienteApi expedienteApi,
                                 ArchivoServiceTemporal archivoService, TarifaService tarifaService,
                                 FolioService folioService, HistorialService historialService,
                                 SesionTemporal sesion) {
        this.solicitudRepository = solicitudRepository;
        this.expedienteApi = expedienteApi;
        this.archivoService = archivoService;
        this.tarifaService = tarifaService;
        this.folioService = folioService;
        this.historialService = historialService;
        this.sesion = sesion;
    }

    /** Paso 1: buscar el expediente para saber si es público o privado. */
    public ExpedienteDto buscarExpediente(String numeroExpediente) {
        if (numeroExpediente == null || numeroExpediente.isBlank()) {
            throw new ReglaNegocioException("Escriba el número de expediente");
        }
        return expedienteApi.buscar(numeroExpediente.trim())
                .orElseThrow(() -> new ReglaNegocioException("El expediente no fue encontrado"));
    }

    /** Paso 2: registrar la solicitud con los datos del solicitante (y la INE si es privado). */
    @Transactional
    public SolicitudCopia registrar(SolicitudForm form, MultipartFile ine) {
        ExpedienteDto expediente = buscarExpediente(form.getNumeroExpediente());
        boolean privado = expediente.tipo() == ExpedienteApi.TipoExpediente.PRIVADO;
        Tarifa tarifa = tarifaService.vigente();

        SolicitudCopia solicitud = new SolicitudCopia();
        solicitud.setFolio(folioService.folioSolicitud());
        // Datos esenciales del expediente (no se guardan las partes ni los documentos)
        solicitud.setNumeroExpediente(expediente.numeroExpediente());
        solicitud.setTipo(expediente.tipo());
        solicitud.setJuzgadoId(expediente.juzgadoId());
        solicitud.setJuzgado(expediente.juzgado());
        solicitud.setMateriaId(expediente.materiaId());
        solicitud.setMateria(expediente.materia());
        // Solicitante
        solicitud.setSolicitanteNombre(form.getSolicitanteNombre().trim().replaceAll("\\s+", " "));
        solicitud.setSolicitanteRfc(form.getSolicitanteRfc().trim().toUpperCase(Locale.ROOT));
        solicitud.setSolicitanteCorreo(form.getSolicitanteCorreo().trim().toLowerCase(Locale.ROOT));
        // Costo congelado con la tarifa vigente
        solicitud.setTarifaId(tarifa.getId());
        solicitud.setCosto(tarifa.getMonto());
        solicitud.setCapturadoPor(sesion.usuarioId());
        solicitud.setFechaSolicitud(Formatos.ahora());

        if (privado) {
            if (ine == null || ine.isEmpty()) {
                throw new ReglaNegocioException("El expediente es privado: adjunte la identificación oficial (INE) del solicitante");
            }
            solicitud.setIneArchivoId(archivoService.guardar(MODULO, ine, TIPOS_INE, TAMANO_MAXIMO_INE_MB));
            solicitud.setEstado(EstadoSolicitud.EN_ESPERA_VALIDACION);
        } else {
            solicitud.setEstado(EstadoSolicitud.LISTO_PARA_PAGO);
        }

        SolicitudCopia guardada = solicitudRepository.save(solicitud);
        historialService.registrarCreacion(guardada, privado
                ? "Solicitud capturada. Debe esperar la aprobación antes de pagar"
                : "Solicitud capturada. Expediente público: pasa directo al pago");
        return guardada;
    }

    @Transactional(readOnly = true)
    public Optional<SolicitudCopia> buscar(Long id) {
        return solicitudRepository.findById(id);
    }

    /** Para los services: la solicitud o un error de negocio. */
    public SolicitudCopia obtener(Long id) {
        return buscar(id).orElseThrow(() -> new ReglaNegocioException("No se encontraron registros"));
    }

    /** Registro de solicitudes con filtros opcionales (null o vacío = no filtra). */
    @Transactional(readOnly = true)
    public Page<SolicitudCopia> listar(String texto, EstadoSolicitud estado, int pagina, int tamano) {
        int tamanoValido = TAMANOS_PAGINA.contains(tamano) ? tamano : TAMANO_PAGINA;
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return solicitudRepository.buscar(filtro, estado, PageRequest.of(Math.max(pagina, 0), tamanoValido));
    }

    /** Cuántas solicitudes hay en cada estado, en el orden del enum (para el inicio y los filtros). */
    @Transactional(readOnly = true)
    public Map<EstadoSolicitud, Long> totalesPorEstado() {
        Map<EstadoSolicitud, Long> totales = new LinkedHashMap<>();
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            totales.put(estado, solicitudRepository.countByEstado(estado));
        }
        return totales;
    }

    public long total() {
        return solicitudRepository.count();
    }
}
