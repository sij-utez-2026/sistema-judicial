package mx.edu.utez.sij.peritajes.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Formulario de registro (RF 3.1) y edición (RF 3.3) de peritos.
 *
 * En la edición, el correo y el número de registro se muestran solo de lectura y el service los ignora:
 * no se pueden cambiar.
 * Mensajes según los criterios de aceptación del DFR (RF 3.1 y 3.3) cuando los hay.
 */
public class PeritoForm {

    @NotBlank(message = "El número de registro es obligatorio")
    @Pattern(regexp = "^(?i)PER-\\d{4}$", message = "El número de registro debe tener el formato PER-0000")
    private String numeroRegistro;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no debe pasar de 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 60, message = "El apellido paterno no debe pasar de 60 caracteres")
    private String apellidoPaterno;

    @Size(max = 60, message = "El apellido materno no debe pasar de 60 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no debe pasar de 150 caracteres")
    private String correo;

    // 10 dígitos; se permiten espacios entre ellos (777 000 0186)
    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Pattern(regexp = "^\\s*(\\d\\s*){10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;

    // Especialidades en texto libre. Llegan como especialidades[0], especialidades[1], ...
    // (con índice, para que una coma dentro del texto no la parta en dos)
    // El largo máximo (100) se valida en PeritoService
    private List<String> especialidades = new ArrayList<>(List.of(""));

    @NotEmpty(message = "Seleccione al menos una materia para la asignación")
    private Set<Long> materiasIds = new LinkedHashSet<>();

    // RF 3.1: "la especialidad es un campo obligatorio" (al menos una que no esté vacía)
    @AssertTrue(message = "La especialidad es un campo obligatorio: capture al menos una")
    public boolean isEspecialidadCapturada() {
        return especialidades != null
                && especialidades.stream().anyMatch(e -> e != null && !e.isBlank());
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

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
    }

    public Set<Long> getMateriasIds() {
        return materiasIds;
    }

    public void setMateriasIds(Set<Long> materiasIds) {
        this.materiasIds = materiasIds;
    }
}
