package mx.edu.utez.sij.copias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Colores de las pantallas de Copias (COPIAS_ADMIN). Cada campo es un color "#RRGGBB".
 * Los nombres de la paleta institucional van entre paréntesis en ColoresCopiasService.
 */
public class ColoresForm {

    private static final String HEX = "^#[0-9A-Fa-f]{6}$";
    private static final String MENSAJE = "Use un color con formato #RRGGBB";

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String primario;

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String titulos;

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String botones;

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String acento;

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String bordes;

    @NotBlank(message = MENSAJE)
    @Pattern(regexp = HEX, message = MENSAJE)
    private String avisos;

    public String getPrimario() {
        return primario;
    }

    public void setPrimario(String primario) {
        this.primario = primario;
    }

    public String getTitulos() {
        return titulos;
    }

    public void setTitulos(String titulos) {
        this.titulos = titulos;
    }

    public String getBotones() {
        return botones;
    }

    public void setBotones(String botones) {
        this.botones = botones;
    }

    public String getAcento() {
        return acento;
    }

    public void setAcento(String acento) {
        this.acento = acento;
    }

    public String getBordes() {
        return bordes;
    }

    public void setBordes(String bordes) {
        this.bordes = bordes;
    }

    public String getAvisos() {
        return avisos;
    }

    public void setAvisos(String avisos) {
        this.avisos = avisos;
    }
}
