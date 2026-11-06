package mx.edu.utez.sij.copias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entrega física de la copia (tabla cop_entrega). Después de entregar, el folio queda inhabilitado (RN-03).
 */
@Entity
@Table(name = "cop_entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false, unique = true)
    private Long solicitudId;

    @Column(name = "entregado_por", nullable = false)
    private Long entregadoPor;

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

    public Long getEntregadoPor() {
        return entregadoPor;
    }

    public void setEntregadoPor(Long entregadoPor) {
        this.entregadoPor = entregadoPor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
