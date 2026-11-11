package mx.edu.utez.sij.copias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tarifa de la copia certificada (tabla cop_tarifa). La vigente es la más reciente que ya empezó.
 * Quién la configura está pendiente (pendiente 17): por ahora DatosInicialesCopias crea una de $320.
 */
@Entity
@Table(name = "cop_tarifa")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "creada_por")
    private Long creadaPor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public void setVigenteDesde(LocalDate vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
    }

    public Long getCreadaPor() {
        return creadaPor;
    }

    public void setCreadaPor(Long creadaPor) {
        this.creadaPor = creadaPor;
    }
}
