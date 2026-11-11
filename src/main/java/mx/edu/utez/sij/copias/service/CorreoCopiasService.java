package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.model.TipoCorreo;
import mx.edu.utez.sij.copias.repository.CorreoEnviadoRepository;
import mx.edu.utez.sij.copias.temporal.CorreoTemporal;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Correos al solicitante: el comprobante de pago y el aviso de que su copia está lista para recoger.
 * Cada envío queda en cop_correo.
 *
 * TODO núcleo: enviar con el servicio de correo del núcleo, con plantillas en templates/correo/copias/
 * y el comprobante en PDF adjunto (PdfService). Hoy el correo se imprime en la consola.
 */
@Service
public class CorreoCopiasService {

    private final CorreoTemporal correo;
    private final CorreoEnviadoRepository correoRepository;
    private final SesionTemporal sesion;

    public CorreoCopiasService(CorreoTemporal correo, CorreoEnviadoRepository correoRepository, SesionTemporal sesion) {
        this.correo = correo;
        this.correoRepository = correoRepository;
        this.sesion = sesion;
    }

    /** Mismos datos esenciales que el comprobante impreso. Nunca la INE. */
    @Transactional
    public CorreoEnviado enviarComprobante(SolicitudCopia solicitud, Pago pago) {
        String cuerpo = """
                Estimado(a) %s:

                Su pago de copias certificadas quedó registrado.

                Folio del comprobante: %s
                Folio de la solicitud: %s
                Expediente:            %s
                Juzgado:               %s
                Materia:               %s
                Importe:               %s
                Fecha de pago:         %s

                Pago simulado: no se realizó ningún cargo.
                Presente este comprobante y una identificación oficial al recoger su copia en el juzgado.
                """.formatted(solicitud.getSolicitanteNombre(), pago.getFolioPago(), solicitud.getFolio(),
                solicitud.getNumeroExpediente(), solicitud.getJuzgado(), solicitud.getMateria(),
                pago.getMontoTexto(), pago.getFechaTexto());
        return enviar(solicitud, TipoCorreo.COMPROBANTE, "Comprobante de pago " + pago.getFolioPago(), cuerpo);
    }

    @Transactional
    public CorreoEnviado enviarListaParaRecoger(SolicitudCopia solicitud) {
        String cuerpo = """
                Estimado(a) %s:

                Su copia certificada del expediente %s ya está lista.

                Folio de la solicitud: %s
                Lugar de entrega:      %s

                Acuda con su comprobante de pago y una identificación oficial.
                """.formatted(solicitud.getSolicitanteNombre(), solicitud.getNumeroExpediente(),
                solicitud.getFolio(), solicitud.getJuzgado());
        return enviar(solicitud, TipoCorreo.LISTA_PARA_RECOGER, "Su copia certificada está lista para recoger", cuerpo);
    }

    @Transactional(readOnly = true)
    public List<CorreoEnviado> enviados(Long solicitudId) {
        return correoRepository.findBySolicitudIdOrderByEnviadoEnDesc(solicitudId);
    }

    private CorreoEnviado enviar(SolicitudCopia solicitud, TipoCorreo tipo, String asunto, String cuerpo) {
        boolean exito = correo.enviar(solicitud.getSolicitanteCorreo(), asunto, cuerpo);
        CorreoEnviado registro = new CorreoEnviado();
        registro.setSolicitudId(solicitud.getId());
        registro.setTipo(tipo);
        registro.setDestinatario(solicitud.getSolicitanteCorreo());
        registro.setEnviadoPor(sesion.usuarioId());
        registro.setEnviadoEn(Formatos.ahora());
        registro.setExito(exito);
        return correoRepository.save(registro);
    }
}
