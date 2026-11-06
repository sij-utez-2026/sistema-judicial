package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.dto.ColoresForm;
import mx.edu.utez.sij.copias.dto.PersonalForm;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Administración del área: trabajadores y colores (COPIAS_ADMIN). */
class AdminCopiasServiceTest {

    private final CopiasPrueba p = new CopiasPrueba();

    private PersonalForm trabajador(String nombre, String correo) {
        PersonalForm form = new PersonalForm();
        form.setNombre(nombre);
        form.setCorreo(correo);
        return form;
    }

    @Test
    void altaBajaYReactivacionDeTrabajadores() {
        Usuario ana = p.personalService.registrar(trabajador(" Ana  Ruiz ", " Ana@Example.com "));
        assertEquals("Ana Ruiz", ana.getNombre());
        assertEquals("ana@example.com", ana.getCorreo());
        assertTrue(ana.getRoles().contains(PersonalCopiasService.ROL_PERSONAL));

        assertThrows(ReglaNegocioException.class,
                () -> p.personalService.registrar(trabajador("Otra", "ANA@example.com")));

        assertFalse(p.personalService.cambiarEstado(ana.getId(), false).isActivo());
        assertTrue(p.personalService.cambiarEstado(ana.getId(), true).isActivo());
        assertThrows(ReglaNegocioException.class, () -> p.personalService.cambiarEstado(99L, false));
        assertEquals(1, p.personalService.trabajadores().size());
    }

    @Test
    void coloresIniciadosConLaPaletaYSeRestablecen() {
        assertEquals("#35483B", p.coloresService.actuales().get(ColoresCopiasService.PRIMARIO));

        ColoresForm form = p.coloresService.formulario();
        form.setBotones("#00ff00");
        p.coloresService.guardar(form);
        assertEquals("#00FF00", p.coloresService.actuales().get(ColoresCopiasService.BOTONES));

        p.coloresService.restablecer();
        assertEquals("#651747", p.coloresService.actuales().get(ColoresCopiasService.BOTONES));
    }

    @Test
    void soloAceptaColoresHexadecimales() {
        ColoresForm form = p.coloresService.formulario();
        // Evita que se cuele CSS dentro de la etiqueta <style>
        form.setPrimario("red;}body{display:none");
        assertThrows(ReglaNegocioException.class, () -> p.coloresService.guardar(form));
    }
}
