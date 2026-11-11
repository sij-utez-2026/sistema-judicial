package mx.edu.utez.sij.copias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.TipoExpediente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Solicitud de copia certificada (tabla cop_solicitud, docs/modulos/copias-diccionario-datos.md).
 *
 * Ya tiene las anotaciones de JPA: hoy no hacen nada porque la base de datos está apagada
 * (application.properties) y los datos viven en SolicitudCopiaRepositoryMemoria.
 *
 * Del expediente de Oficialía solo se guardan los datos esenciales (número, tipo, juzgado y materia);
 * las partes se consultan con ExpedienteApi al validar y no se guardan aquí.
 * Columnas *_por: id de core_usuario (FK cuando exista el núcleo).
 */
@Entity
@Table(name = "cop_solicitud")
public class SolicitudCopia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CC-AAAA-XXXXXX, con parte aleatoria para que no se pueda adivinar
    @Column(name = "folio", nullable = false, unique = true, length = 20)
    private String folio;

    // Datos esenciales del expediente (Oficialía, sin FK)
    @Column(name = "numero_expediente", nullable = false, length = 30)
    private String numeroExpediente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoExpediente tipo;

    @Column(name = "juzgado_id", nullable = false)
    private Long juzgadoId;

    // Nombre del juzgado y de la materia al momento de la solicitud, para el registro y el comprobante
    @Column(name = "juzgado", nullable = false, length = 150)
    private String juzgado;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(name = "materia", nullable = false, length = 80)
    private String materia;

    // Solicitante (no tiene cuenta: el trabajador captura sus datos)
    @Column(name = "solicitante_nombre", nullable = false, length = 150)
    private String solicitanteNombre;

    @Column(name = "solicitante_rfc", nullable = false, length = 13)
    private String solicitanteRfc;

    @Column(name = "solicitante_correo", nullable = false, length = 150)
    private String solicitanteCorreo;

    // INE: id de core_archivo. Solo en expedientes privados
    @Column(name = "ine_archivo_id")
    private Long ineArchivoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Column(name = "tarifa_id", nullable = false)
    private Long tarifaId;

    // Costo congelado al crear la solicitud
    @Column(name = "costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "capturado_por", nullable = false)
    private Long capturadoPor;

    @Column(name = "validado_por")
    private Long validadoPor;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public boolean isPrivada() {
        return tipo == TipoExpediente.PRIVADO;
    }

    public String getTipoTexto() {
        return isPrivada() ? "Privado" : "Público";
    }

    public String getCostoTexto() {
        return Formatos.dinero(costo);
    }

    public String getFechaSolicitudTexto() {
        return Formatos.fechaHora(fechaSolicitud);
    }

    public String getFechaValidacionTexto() {
        return Formatos.fechaHora(fechaValidacion);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public TipoExpediente getTipo() {
        return tipo;
    }

    public void setTipo(TipoExpediente tipo) {
        this.tipo = tipo;
    }

    public Long getJuzgadoId() {
        return juzgadoId;
    }

    public void setJuzgadoId(Long juzgadoId) {
        this.juzgadoId = juzgadoId;
    }

    public String getJuzgado() {
        return juzgado;
    }

    public void setJuzgado(String juzgado) {
        this.juzgado = juzgado;
    }

    public Long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Long materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getSolicitanteNombre() {
        return solicitanteNombre;
    }

    public void setSolicitanteNombre(String solicitanteNombre) {
        this.solicitanteNombre = solicitanteNombre;
    }

    public String getSolicitanteRfc() {
        return solicitanteRfc;
    }

    public void setSolicitanteRfc(String solicitanteRfc) {
        this.solicitanteRfc = solicitanteRfc;
    }

    public String getSolicitanteCorreo() {
        return solicitanteCorreo;
    }

    public void setSolicitanteCorreo(String solicitanteCorreo) {
        this.solicitanteCorreo = solicitanteCorreo;
    }

    public Long getIneArchivoId() {
        return ineArchivoId;
    }

    public void setIneArchivoId(Long ineArchivoId) {
        this.ineArchivoId = ineArchivoId;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public Long getTarifaId() {
        return tarifaId;
    }

    public void setTarifaId(Long tarifaId) {
        this.tarifaId = tarifaId;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public Long getCapturadoPor() {
        return capturadoPor;
    }

    public void setCapturadoPor(Long capturadoPor) {
        this.capturadoPor = capturadoPor;
    }

    public Long getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(Long validadoPor) {
        this.validadoPor = validadoPor;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
