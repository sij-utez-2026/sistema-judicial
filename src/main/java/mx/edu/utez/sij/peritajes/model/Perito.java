package mx.edu.utez.sij.peritajes.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Perito del padrón (RF 3.1 – 3.4).
 *
 * TEMPORAL: por ahora se guarda en memoria (PeritoRepositoryMemoria). Cuando esté el modelo relacional:
 * - se agrega @Entity y @Table(name = "per_...") con los nombres de columnas acordados,
 * - especialidades y materiasIds se mapean con @ElementCollection (de materias solo el id, sin FK a Oficialía),
 * - correo y numeroRegistro llevan unique = true.
 */
public class Perito {

    private Long id;
    private String numeroRegistro;      // PER-0000, único y fijo
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;     // opcional
    private String correo;              // único y fijo
    private String telefono;            // solo los 10 dígitos
    // Especialidades en texto libre: área o estudios del perito (al menos una)
    private Set<String> especialidades = new LinkedHashSet<>();
    // Materias de intervención: solo los id (el catálogo es de Oficialía)
    private Set<Long> materiasIds = new LinkedHashSet<>();
    private boolean activo = true;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public String getNombreCompleto() {
        String completo = nombre + " " + apellidoPaterno;
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) {
            completo += " " + apellidoMaterno;
        }
        return completo;
    }

    /** Teléfono como se muestra en pantalla: 777 000 0186. */
    public String getTelefonoFormateado() {
        if (telefono == null || telefono.length() != 10) {
            return telefono;
        }
        return telefono.substring(0, 3) + " " + telefono.substring(3, 6) + " " + telefono.substring(6);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Set<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<String> especialidades) {
        this.especialidades = especialidades;
    }

    public Set<Long> getMateriasIds() {
        return materiasIds;
    }

    public void setMateriasIds(Set<Long> materiasIds) {
        this.materiasIds = materiasIds;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
