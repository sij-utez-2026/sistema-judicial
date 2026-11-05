package mx.edu.utez.sij.peritajes.service;

import mx.edu.utez.sij.peritajes.dto.PeritoForm;
import mx.edu.utez.sij.peritajes.model.Perito;
import mx.edu.utez.sij.peritajes.repository.PeritoRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeritoServiceTest {

    private PeritoService peritoService;

    @BeforeEach
    void preparar() {
        peritoService = new PeritoService(new PeritoRepositoryMemoria(), new CatalogoMateriasTemporal());
    }

    private PeritoForm form(String registro, String nombre, String correo, Long... materias) {
        PeritoForm form = new PeritoForm();
        form.setNumeroRegistro(registro);
        form.setNombre(nombre);
        form.setApellidoPaterno("Neri");
        form.setApellidoMaterno("Campos");
        form.setCorreo(correo);
        form.setTelefono("777 000 0186");
        form.setEspecialidades(new ArrayList<>(List.of("Psicología")));
        form.setMateriasIds(Set.of(materias));
        return form;
    }

    @Test
    void registraPeritoActivoConDatosNormalizados() {
        PeritoForm datos = form("per-0057", " Sofía ", " Sofia.Neri@PJ.mx ", 2L);
        datos.setApellidoMaterno("  ");

        Perito perito = peritoService.registrar(datos);

        assertTrue(perito.isActivo());
        assertEquals("PER-0057", perito.getNumeroRegistro());
        assertEquals("Sofía", perito.getNombre());
        assertNull(perito.getApellidoMaterno());
        assertEquals("Sofía Neri", perito.getNombreCompleto());
        assertEquals("sofia.neri@pj.mx", perito.getCorreo());
        assertEquals("7770000186", perito.getTelefono());
        assertEquals("777 000 0186", perito.getTelefonoFormateado());
    }

    @Test
    void noPermiteNumeroDeRegistroNiCorreoRepetidos() {
        peritoService.registrar(form("PER-0001", "Ana", "ana@pj.mx", 1L));

        assertThrows(ReglaNegocioException.class,
                () -> peritoService.registrar(form("per-0001", "Luis", "luis@pj.mx", 1L)));
        assertThrows(ReglaNegocioException.class,
                () -> peritoService.registrar(form("PER-0002", "Luis", "ANA@pj.mx", 1L)));
    }

    @Test
    void editarNoCambiaCorreoNiNumeroDeRegistro() {
        Perito perito = peritoService.registrar(form("PER-0001", "Ana", "ana@pj.mx", 1L));

        PeritoForm cambios = form("PER-9999", "Ana María", "otro@pj.mx", 3L);
        cambios.setEspecialidades(new ArrayList<>(List.of("Criminalística", "Balística")));
        Perito editado = peritoService.actualizar(perito.getId(), cambios);

        assertEquals("Ana María", editado.getNombre());
        assertEquals(Set.of("Criminalística", "Balística"), editado.getEspecialidades());
        assertEquals(Set.of(3L), editado.getMateriasIds());
        assertEquals("ana@pj.mx", editado.getCorreo());
        assertEquals("PER-0001", editado.getNumeroRegistro());
    }

    @Test
    void limpiaEspecialidadesVaciasYRepetidas() {
        PeritoForm datos = form("PER-0001", "Ana", "ana@pj.mx", 1L);
        datos.setEspecialidades(new ArrayList<>(java.util.Arrays.asList(" Psicología ", "", null, "psicología", "Grafología")));

        Perito perito = peritoService.registrar(datos);

        assertEquals(List.of("Psicología", "Grafología"), new ArrayList<>(perito.getEspecialidades()));
    }

    @Test
    void exigeAlMenosUnaEspecialidad() {
        PeritoForm datos = form("PER-0001", "Ana", "ana@pj.mx", 1L);
        datos.setEspecialidades(new ArrayList<>(List.of(" ", "")));

        assertFalse(datos.isEspecialidadCapturada());
        assertThrows(ReglaNegocioException.class, () -> peritoService.registrar(datos));
    }

    @Test
    void tamanoDePaginaPermitido() {
        for (int i = 1; i <= 12; i++) {
            peritoService.registrar(form(String.format("PER-%04d", i), "P" + i, "p" + i + "@pj.mx", 1L));
        }

        assertEquals(10, peritoService.listar(null, null, null, null, 0, 10).getContent().size());
        assertEquals(12, peritoService.listar(null, null, null, null, 0, 25).getContent().size());
        // Un tamaño que no está en la lista usa el de 25
        assertEquals(25, peritoService.listar(null, null, null, null, 0, 7).getSize());
    }

    @Test
    void rechazaMateriaQueNoEstaEnElCatalogo() {
        assertThrows(ReglaNegocioException.class,
                () -> peritoService.registrar(form("PER-0001", "Ana", "ana@pj.mx", 99L)));
    }

    @Test
    void bajaYReactivacion() {
        Perito perito = peritoService.registrar(form("PER-0001", "Ana", "ana@pj.mx", 1L));

        assertFalse(peritoService.cambiarEstado(perito.getId(), false).isActivo());
        assertEquals(1, peritoService.totalInactivos());
        assertTrue(peritoService.cambiarEstado(perito.getId(), true).isActivo());
        assertEquals(1, peritoService.totalActivos());
    }

    @Test
    void filtraPorTextoEspecialidadMateriaYEstado() {
        peritoService.registrar(form("PER-0001", "Ana", "ana@pj.mx", 1L));
        PeritoForm luis = form("PER-0002", "Luis", "luis@pj.mx", 2L);
        luis.setEspecialidades(new ArrayList<>(List.of("Contabilidad", "Auditoría")));
        Perito l = peritoService.registrar(luis);
        peritoService.cambiarEstado(l.getId(), false);

        assertEquals(1, peritoService.listar("luis", null, null, null, 0, 25).getTotalElements());
        assertEquals(1, peritoService.listar("per-0001", null, null, null, 0, 25).getTotalElements());
        assertEquals(1, peritoService.listar(null, "Contabilidad", null, null, 0, 25).getTotalElements());
        assertEquals(1, peritoService.listar(null, null, 1L, null, 0, 25).getTotalElements());
        assertEquals(1, peritoService.listar(null, null, null, true, 0, 25).getTotalElements());
        assertEquals(2, peritoService.listar(" ", "", null, null, 0, 25).getTotalElements());
        assertEquals(List.of("Auditoría", "Contabilidad", "Psicología"), peritoService.especialidadesRegistradas());
        assertEquals(Map.of("Civil", 1L, "Familiar", 1L, "Mercantil", 0L, "Penal", 0L), peritoService.totalesPorMateria());
    }
}
