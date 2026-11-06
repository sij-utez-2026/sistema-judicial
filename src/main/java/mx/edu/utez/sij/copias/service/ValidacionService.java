package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import mx.edu.utez.sij.copias.temporal.ArchivoServiceTemporal;
import mx.edu.utez.sij.copias.temporal.ArchivoServiceTemporal.Archivo;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.ExpedienteDto;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.ParteDto;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Visto bueno de solicitudes privadas (RF 3.1, COPIAS_PERSONAL).
 * El trabajador compara los datos del solicitante y su INE con las partes del expediente y aprueba o rechaza.
 * Aprobar → "Aprobado" (se habilita el pago). Rechazar → "Cancelado" (bloquea el pago para siempre).
 */
@Service
public class ValidacionService {

    public static final int MOTIVO_MAXIMO = 500;

    private final SolicitudCopiaRepository solicitudRepository;
    private final SolicitudCopiaService solicitudService;
    private final HistorialService historialService;
    private final ExpedienteApi expedienteApi;
    private final ArchivoServiceTemporal archivoService;
    private final SesionTemporal sesion;

    public ValidacionService(SolicitudCopiaRepository solicitudRepository, SolicitudCopiaService solicitudService,
                             HistorialService historialService, ExpedienteApi expedienteApi,
                             ArchivoServiceTemporal archivoService, SesionTemporal sesion) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudService = solicitudService;
        this.historialService = historialService;
        this.expedienteApi = expedienteApi;
        this.archivoService = archivoService;
        this.sesion = sesion;
    }

    /** Bandeja: privadas en espera, las más antiguas primero. */
    @Transactional(readOnly = true)
    public List<SolicitudCopia> pendientes() {
        return solicitudRepository.findByEstadoInOrderByFechaSolicitudAsc(List.of(EstadoSolicitud.EN_ESPERA_VALIDACION));
    }

    /** Expediente completo para comparar (las partes solo se muestran aquí, nunca se guardan en Copias). */
    public Optional<ExpedienteDto> expediente(SolicitudCopia solicitud) {
        return expedienteApi.buscar(solicitud.getNumeroExpediente());
    }

    /** Apoyo para validar: partes del expediente con el mismo RFC que el solicitante. */
    public List<ParteDto> partesQueCoinciden(SolicitudCopia solicitud) {
        return expediente(solicitud).map(e -> e.partes().stream()
                        .filter(p -> p.rfc() != null && p.rfc().equalsIgnoreCase(solicitud.getSolicitanteRfc()))
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public SolicitudCopia aprobar(Long id) {
        SolicitudCopia solicitud = enEspera(id);
        solicitud.setValidadoPor(sesion.usuarioId());
        solicitud.setFechaValidacion(Formatos.ahora());
        return historialService.cambiarEstado(solicitud, EstadoSolicitud.APROBADO, "Visto bueno: el solicitante es parte del expediente");
    }

    @Transactional
    public SolicitudCopia rechazar(Long id, String motivo) {
        String texto = motivo == null ? "" : motivo.trim();
        if (texto.isEmpty()) {
            throw new ReglaNegocioException("Indique el motivo del rechazo");
        }
        if (texto.length() > MOTIVO_MAXIMO) {
            throw new ReglaNegocioException("El motivo no debe pasar de " + MOTIVO_MAXIMO + " caracteres");
        }
        SolicitudCopia solicitud = enEspera(id);
        solicitud.setValidadoPor(sesion.usuarioId());
        solicitud.setFechaValidacion(Formatos.ahora());
        solicitud.setMotivoRechazo(texto);
        return historialService.cambiarEstado(solicitud, EstadoSolicitud.CANCELADO, "Rechazada: " + texto);
    }

    /**
     * INE de la solicitud. Solo la ve COPIAS_PERSONAL (la ruta lleva @PreAuthorize cuando exista Spring Security)
     * y nunca aparece en el comprobante.
     */
    @Transactional(readOnly = true)
    public Archivo ine(Long id) {
        SolicitudCopia solicitud = solicitudService.obtener(id);
        if (solicitud.getIneArchivoId() == null) {
            throw new ReglaNegocioException("La solicitud no tiene identificación");
        }
        return archivoService.descargar(solicitud.getIneArchivoId())
                .orElseThrow(() -> new ReglaNegocioException("No se encontró la identificación"));
    }

    private SolicitudCopia enEspera(Long id) {
        SolicitudCopia solicitud = solicitudService.obtener(id);
        if (solicitud.getEstado() != EstadoSolicitud.EN_ESPERA_VALIDACION) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getFolio() + " ya no está en espera de validación ("
                    + solicitud.getEstado().getTexto() + ")");
        }
        return solicitud;
    }
}
