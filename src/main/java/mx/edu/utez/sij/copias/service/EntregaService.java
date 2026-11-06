package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.model.Entrega;
import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.repository.EntregaRepository;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Entrega física (RF 5.1). Después del pago hay dos caminos:
 * - la copia está disponible: se imprime y se entrega al momento → "Entregado";
 * - no está disponible: queda "Pagado y en preparación"; cuando esté lista se avisa por correo
 *   ("Lista para recoger") y se entrega cuando el solicitante llegue.
 * Al entregar, el folio queda inhabilitado (RN-03).
 */
@Service
public class EntregaService {

    private static final List<EstadoSolicitud> PENDIENTES =
            List.of(EstadoSolicitud.PAGADO_EN_PREPARACION, EstadoSolicitud.LISTO_PARA_RECOGER);

    private final EntregaRepository entregaRepository;
    private final SolicitudCopiaRepository solicitudRepository;
    private final SolicitudCopiaService solicitudService;
    private final HistorialService historialService;
    private final CorreoCopiasService correoService;
    private final SesionTemporal sesion;

    public EntregaService(EntregaRepository entregaRepository, SolicitudCopiaRepository solicitudRepository,
                          SolicitudCopiaService solicitudService, HistorialService historialService,
                          CorreoCopiasService correoService, SesionTemporal sesion) {
        this.entregaRepository = entregaRepository;
        this.solicitudRepository = solicitudRepository;
        this.solicitudService = solicitudService;
        this.historialService = historialService;
        this.correoService = correoService;
        this.sesion = sesion;
    }

    /** Pagadas que falta entregar, las más antiguas primero. */
    @Transactional(readOnly = true)
    public List<SolicitudCopia> pendientes() {
        return solicitudRepository.findByEstadoInOrderByFechaSolicitudAsc(PENDIENTES);
    }

    /** Avisa por correo que la copia ya está lista. Se puede repetir si el solicitante no lo recibió. */
    @Transactional
    public CorreoEnviado avisarListaParaRecoger(Long solicitudId) {
        SolicitudCopia solicitud = pendienteDeEntrega(solicitudId);
        CorreoEnviado correo = correoService.enviarListaParaRecoger(solicitud);
        if (!correo.isExito()) {
            throw new ReglaNegocioException("No se pudo enviar el correo. Intente de nuevo");
        }
        if (solicitud.getEstado() == EstadoSolicitud.PAGADO_EN_PREPARACION) {
            historialService.cambiarEstado(solicitud, EstadoSolicitud.LISTO_PARA_RECOGER,
                    "Aviso enviado a " + solicitud.getSolicitanteCorreo());
        }
        return correo;
    }

    /** Registra la entrega física de la copia impresa. */
    @Transactional
    public Entrega entregar(Long solicitudId) {
        SolicitudCopia solicitud = pendienteDeEntrega(solicitudId);

        Entrega entrega = new Entrega();
        entrega.setSolicitudId(solicitud.getId());
        entrega.setEntregadoPor(sesion.usuarioId());
        entrega.setFecha(Formatos.ahora());
        Entrega guardada = entregaRepository.save(entrega);

        historialService.cambiarEstado(solicitud, EstadoSolicitud.ENTREGADO, "Copia impresa y entregada en ventanilla");
        return guardada;
    }

    @Transactional(readOnly = true)
    public Optional<Entrega> entregaDe(Long solicitudId) {
        return entregaRepository.findBySolicitudId(solicitudId);
    }

    private SolicitudCopia pendienteDeEntrega(Long solicitudId) {
        SolicitudCopia solicitud = solicitudService.obtener(solicitudId);
        if (solicitud.getEstado() == EstadoSolicitud.ENTREGADO) {
            throw new ReglaNegocioException("La copia ya fue entregada: el folio " + solicitud.getFolio() + " está inhabilitado");
        }
        if (!solicitud.getEstado().pendienteDeEntrega()) {
            throw new ReglaNegocioException("La solicitud aún no está pagada (" + solicitud.getEstado().getTexto() + ")");
        }
        return solicitud;
    }
}
