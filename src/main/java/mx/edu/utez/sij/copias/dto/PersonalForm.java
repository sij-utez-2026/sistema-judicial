package mx.edu.utez.sij.copias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Alta de un trabajador del área (COPIAS_PERSONAL) por el administrador (COPIAS_ADMIN). */
public class PersonalForm {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no debe pasar de 150 caracteres")
    private String nombre;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no debe pasar de 150 caracteres")
    private String correo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
