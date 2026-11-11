package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.dto.ColoresForm;
import mx.edu.utez.sij.copias.model.ColorArea;
import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.repository.ColorAreaRepository;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Colores de las pantallas de Copias que configura COPIAS_ADMIN (tabla cop_color).
 * Solo cambian las pantallas de este módulo: tags/copias/colores.tag pone estas variables CSS encima de las del núcleo.
 * Los valores iniciales son la paleta institucional que acordó el grupo.
 */
@Service
public class ColoresCopiasService {

    // Variable CSS → color inicial. --sij-* son del núcleo; --cop-* son propias de recursos/copias/css/copias.css
    public static final String PRIMARIO = "--sij-primario";
    public static final String TITULOS = "--cop-titulos";
    public static final String BOTONES = "--cop-boton";
    public static final String ACENTO = "--sij-acento";
    public static final String BORDES = "--sij-borde";
    public static final String AVISOS = "--cop-aviso";

    private static final Map<String, String> INICIALES = new LinkedHashMap<>();

    static {
        INICIALES.put(PRIMARIO, "#35483B");   // Bosque: menú lateral
        INICIALES.put(TITULOS, "#76513D");    // Tierra: títulos
        INICIALES.put(BOTONES, "#651747");    // Bugambilia: botones principales
        INICIALES.put(ACENTO, "#B49A5A");     // Dorado: detalles
        INICIALES.put(BORDES, "#B8B0A1");     // Piedra: bordes
        INICIALES.put(AVISOS, "#D8C39A");     // Caña: avisos
    }

    private final ColorAreaRepository colorRepository;
    private final SesionTemporal sesion;

    public ColoresCopiasService(ColorAreaRepository colorRepository, SesionTemporal sesion) {
        this.colorRepository = colorRepository;
        this.sesion = sesion;
    }

    /** Colores actuales (los guardados o, si no hay, los iniciales), en orden fijo. */
    @Transactional(readOnly = true)
    public Map<String, String> actuales() {
        Map<String, String> colores = new LinkedHashMap<>(INICIALES);
        for (ColorArea color : colorRepository.findAll()) {
            if (colores.containsKey(color.getVariable())) {
                colores.put(color.getVariable(), color.getValor());
            }
        }
        return colores;
    }

    public Map<String, String> iniciales() {
        return new LinkedHashMap<>(INICIALES);
    }

    public ColoresForm formulario() {
        return aFormulario(actuales());
    }

    public ColoresForm formularioInicial() {
        return aFormulario(INICIALES);
    }

    @Transactional
    public void guardar(ColoresForm form) {
        guardarColor(PRIMARIO, form.getPrimario());
        guardarColor(TITULOS, form.getTitulos());
        guardarColor(BOTONES, form.getBotones());
        guardarColor(ACENTO, form.getAcento());
        guardarColor(BORDES, form.getBordes());
        guardarColor(AVISOS, form.getAvisos());
    }

    @Transactional
    public void restablecer() {
        INICIALES.forEach(this::guardarColor);
    }

    private ColoresForm aFormulario(Map<String, String> colores) {
        ColoresForm form = new ColoresForm();
        form.setPrimario(colores.get(PRIMARIO));
        form.setTitulos(colores.get(TITULOS));
        form.setBotones(colores.get(BOTONES));
        form.setAcento(colores.get(ACENTO));
        form.setBordes(colores.get(BORDES));
        form.setAvisos(colores.get(AVISOS));
        return form;
    }

    private void guardarColor(String variable, String valor) {
        // También se revisa aquí: el valor se escribe dentro de una etiqueta <style>
        if (valor == null || !valor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new ReglaNegocioException("Use un color con formato #RRGGBB");
        }
        ColorArea color = colorRepository.findByVariable(variable).orElseGet(ColorArea::new);
        color.setVariable(variable);
        color.setValor(valor.toUpperCase(Locale.ROOT));
        color.setActualizadoPor(sesion.usuarioId());
        color.setActualizadoEn(Formatos.ahora());
        colorRepository.save(color);
    }
}
