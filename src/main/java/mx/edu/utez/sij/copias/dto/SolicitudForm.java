package mx.edu.utez.sij.copias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del solicitante que captura el trabajador (RF 2.1 pública y 2.2 privada).
 * Se piden los mismos en los dos casos para llevar el registro de quién solicitó; la INE solo en privadas
 * (llega aparte como archivo). El juzgado y la materia salen del expediente, no se capturan.
 */
public class SolicitudForm {

    @NotBlank(message = "El número de expediente es obligatorio")
    private String numeroExpediente;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no debe pasar de 150 caracteres")
    private String solicitanteNombre;

    // Persona física (13: 4 letras, fecha y homoclave) o moral (12: 3 letras, fecha y homoclave)
    @NotBlank(message = "El RFC es obligatorio")
    @Pattern(regexp = "^\\s*[A-Za-zÑñ&]{3,4}\\d{6}[A-Za-z0-9]{3}\\s*$", message = "El RFC no tiene un formato válido")
    private String solicitanteRfc;

    @NotBlank(message = "El correo es obligatorio para enviar el comprobante")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no debe pasar de 150 caracteres")
    private String solicitanteCorreo;

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
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
}
