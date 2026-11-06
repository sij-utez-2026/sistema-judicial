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

/**
 * Registro de los correos enviados al solicitante (tabla cop_correo) [Propuesta].
 * Sirve para saber si ya se envió el comprobante o el aviso, y para reintentar si falló.
 */
@Entity
@Table(name = "cop_correo")
public class CorreoEnviado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCorreo tipo;

    @Column(name = "destinatario", nullable = false, length = 150)
    private String destinatario;

    @Column(name = "enviado_por", nullable = false)
    private Long enviadoPor;

    @Column(name = "enviado_en", nullable = false)
    private LocalDateTime enviadoEn;

    @Column(name = "exito", nullable = false)
    private boolean exito;

    public String getEnviadoEnTexto() {
        return Formatos.fechaHora(enviadoEn);
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

    public TipoCorreo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCorreo tipo) {
        this.tipo = tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Long getEnviadoPor() {
        return enviadoPor;
    }

    public void setEnviadoPor(Long enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    public LocalDateTime getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(LocalDateTime enviadoEn) {
        this.enviadoEn = enviadoEn;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
