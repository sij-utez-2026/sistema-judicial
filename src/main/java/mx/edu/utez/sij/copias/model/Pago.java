package mx.edu.utez.sij.copias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago simulado de una solicitud (tabla cop_pago). No se realiza ningún cargo real.
 * solicitud_id es único: una solicitud no se puede pagar dos veces.
 */
@Entity
@Table(name = "cop_pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false, unique = true)
    private Long solicitudId;

    // CP-AAAA-XXXXXX: folio del comprobante
    @Column(name = "folio_pago", nullable = false, unique = true, length = 20)
    private String folioPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "simulado", nullable = false)
    private boolean simulado = true;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public String getMontoTexto() {
        return Formatos.dinero(monto);
    }

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

    public String getFolioPago() {
        return folioPago;
    }

    public void setFolioPago(String folioPago) {
        this.folioPago = folioPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public boolean isSimulado() {
        return simulado;
    }

    public void setSimulado(boolean simulado) {
        this.simulado = simulado;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Long registradoPor) {
        this.registradoPor = registradoPor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
