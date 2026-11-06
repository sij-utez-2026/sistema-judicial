package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.HistorialSolicitud;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.repository.HistorialSolicitudRepository;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Único lugar donde cambia el estado de una solicitud: guarda el cambio y lo anota en el historial.
 * TODO núcleo: registrar también cada cambio con bitacoraService.registrar(...) en la misma transacción.
 */
@Service
public class HistorialService {

    private final SolicitudCopiaRepository solicitudRepository;
    private final HistorialSolicitudRepository historialRepository;
    private final SesionTemporal sesion;

    public HistorialService(SolicitudCopiaRepository solicitudRepository,
                            HistorialSolicitudRepository historialRepository,
                            SesionTemporal sesion) {
        this.solicitudRepository = solicitudRepository;
        this.historialRepository = historialRepository;
        this.sesion = sesion;
    }

    /** Primer registro del historial, al crear la solicitud (estado anterior nulo). */
    @Transactional
    public void registrarCreacion(SolicitudCopia solicitud, String comentario) {
        anotar(solicitud.getId(), null, solicitud.getEstado(), comentario);
    }

    @Transactional
    public SolicitudCopia cambiarEstado(SolicitudCopia solicitud, EstadoSolicitud nuevo, String comentario) {
        EstadoSolicitud anterior = solicitud.getEstado();
        solicitud.setEstado(nuevo);
        solicitud.setActualizadoEn(Formatos.ahora());
        SolicitudCopia guardada = solicitudRepository.save(solicitud);
        anotar(solicitud.getId(), anterior, nuevo, comentario);
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<HistorialSolicitud> historial(Long solicitudId) {
        return historialRepository.findBySolicitudIdOrderByFechaAsc(solicitudId);
    }

    private void anotar(Long solicitudId, EstadoSolicitud anterior, EstadoSolicitud nuevo, String comentario) {
        HistorialSolicitud registro = new HistorialSolicitud();
        registro.setSolicitudId(solicitudId);
        registro.setEstadoAnterior(anterior);
        registro.setEstadoNuevo(nuevo);
        registro.setUsuarioId(sesion.usuarioId());
        registro.setComentario(comentario);
        registro.setFecha(Formatos.ahora());
        historialRepository.save(registro);
    }
}
