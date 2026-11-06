package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.repository.PagoRepository;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Pago simulado (RF 4.1): no se realiza ningún cargo. Solo se paga una solicitud "Listo para pago" (pública)
 * o "Aprobado" (privada con visto bueno). Al pagar queda "Pagado y en preparación" y el comprobante se envía
 * por correo; también se puede imprimir.
 */
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final SolicitudCopiaService solicitudService;
    private final HistorialService historialService;
    private final FolioService folioService;
    private final CorreoCopiasService correoService;
    private final SesionTemporal sesion;

    public PagoService(PagoRepository pagoRepository, SolicitudCopiaService solicitudService,
                       HistorialService historialService, FolioService folioService,
                       CorreoCopiasService correoService, SesionTemporal sesion) {
        this.pagoRepository = pagoRepository;
        this.solicitudService = solicitudService;
        this.historialService = historialService;
        this.folioService = folioService;
        this.correoService = correoService;
        this.sesion = sesion;
    }

    /** Revisa si la solicitud se puede pagar; si no, explica por qué (para mostrarlo antes de confirmar). */
    public void revisarQueSePuedePagar(SolicitudCopia solicitud) {
        if (pagoRepository.existsBySolicitudId(solicitud.getId())) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getFolio() + " ya fue pagada");
        }
        switch (solicitud.getEstado()) {
            case EN_ESPERA_VALIDACION -> throw new ReglaNegocioException("Debe esperar la aprobación antes de pagar");
            case CANCELADO -> throw new ReglaNegocioException("La solicitud fue cancelada y no se puede pagar");
            case LISTO_PARA_PAGO, APROBADO -> {
                // se puede pagar
            }
            default -> throw new ReglaNegocioException("La solicitud " + solicitud.getFolio() + " ya fue pagada");
        }
    }

    @Transactional
    public Pago pagar(Long solicitudId) {
        SolicitudCopia solicitud = solicitudService.obtener(solicitudId);
        revisarQueSePuedePagar(solicitud);

        Pago pago = new Pago();
        pago.setSolicitudId(solicitud.getId());
        pago.setFolioPago(folioService.folioPago());
        pago.setMonto(solicitud.getCosto());
        pago.setSimulado(true);
        pago.setRegistradoPor(sesion.usuarioId());
        pago.setFecha(Formatos.ahora());
        Pago guardado = pagoRepository.save(pago);

        historialService.cambiarEstado(solicitud, EstadoSolicitud.PAGADO_EN_PREPARACION,
                "Pago simulado " + guardado.getFolioPago());
        correoService.enviarComprobante(solicitud, guardado);
        return guardado;
    }

    /** Volver a mandar el comprobante (por ejemplo, si el solicitante no lo recibió o corrigió su correo). */
    @Transactional
    public CorreoEnviado reenviarComprobante(Long solicitudId) {
        SolicitudCopia solicitud = solicitudService.obtener(solicitudId);
        Pago pago = pagoDe(solicitudId)
                .orElseThrow(() -> new ReglaNegocioException("La solicitud aún no está pagada"));
        CorreoEnviado correo = correoService.enviarComprobante(solicitud, pago);
        if (!correo.isExito()) {
            throw new ReglaNegocioException("No se pudo enviar el correo. Intente de nuevo");
        }
        return correo;
    }

    @Transactional(readOnly = true)
    public Optional<Pago> pagoDe(Long solicitudId) {
        return pagoRepository.findBySolicitudId(solicitudId);
    }
}
