package mx.edu.utez.sij.copias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Cada cambio de estado de una solicitud: quién y cuándo (tabla cop_solicitud_historial). */
@Entity
@Table(name = "cop_solicitud_historial")
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    // Nulo cuando se crea la solicitud
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoSolicitud estadoNuevo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public String getFechaTexto() {
        return Formatos.fechaHora(fecha);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public EstadoSolicitud getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoSolicitud estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoSolicitud getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoSolicitud estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
