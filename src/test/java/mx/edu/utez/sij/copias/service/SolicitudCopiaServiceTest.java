package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.TipoExpediente;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitudCopiaServiceTest {

    private final CopiasPrueba p = new CopiasPrueba();

    @Test
    void publicaPasaDirectoAlPagoConDatosNormalizadosYEsenciales() {
        SolicitudCopia s = p.solicitudService.registrar(CopiasPrueba.form("exp-2026-001"), null);

        assertEquals(EstadoSolicitud.LISTO_PARA_PAGO, s.getEstado());
        assertEquals(TipoExpediente.PUBLICO, s.getTipo());
        assertEquals("EXP-2026-001", s.getNumeroExpediente());
        assertEquals("Juzgado Primero Civil de Cuernavaca", s.getJuzgado());
        assertEquals("Civil", s.getMateria());
        assertEquals("Andrea Castro Ruiz", s.getSolicitanteNombre());
        assertEquals("CARA900721MZ4", s.getSolicitanteRfc());
        assertEquals("andrea@example.com", s.getSolicitanteCorreo());
        assertNull(s.getIneArchivoId());
        assertTrue(s.getFolio().matches("CC-\\d{4}-[A-Z2-9]{6}"));
        assertEquals(1, p.historialService.historial(s.getId()).size());
    }

    @Test
    void publicaIgnoraLaIneAunqueLaAdjunten() {
        SolicitudCopia s = p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-003"), CopiasPrueba.ine());
        assertNull(s.getIneArchivoId());
    }

    @Test
    void privadaExigeIneYQuedaEnEsperaDeValidacion() {
        assertThrows(ReglaNegocioException.class,
                () -> p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-002"), null));
        assertEquals(0, p.solicitudService.total());

        SolicitudCopia s = p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-002"), CopiasPrueba.ine());
        assertEquals(EstadoSolicitud.EN_ESPERA_VALIDACION, s.getEstado());
        assertNotNull(s.getIneArchivoId());
    }

    @Test
    void ineSoloPdfJpgPngHasta5Mb() {
        MockMultipartFile texto = new MockMultipartFile("ine", "ine.txt", "text/plain", new byte[]{1});
        assertThrows(ReglaNegocioException.class,
                () -> p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-004"), texto));

        MockMultipartFile grande = new MockMultipartFile("ine", "ine.pdf", "application/pdf",
                new byte[5 * 1024 * 1024 + 1]);
        assertThrows(ReglaNegocioException.class,
                () -> p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-004"), grande));

        MockMultipartFile jpeg = new MockMultipartFile("ine", "INE.JPEG", "image/jpeg", new byte[]{1});
        assertNotNull(p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-004"), jpeg).getIneArchivoId());
    }

    @Test
    void expedienteInexistente() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-999"), null));
        assertEquals("El expediente no fue encontrado", e.getMessage());
    }

    @Test
    void elCostoSeCongelaConLaTarifaVigente() {
        SolicitudCopia antes = p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-001"), null);
        p.tarifaService.registrar(new BigDecimal("400.00"), LocalDate.of(2026, 2, 1), 1L);
        SolicitudCopia despues = p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-001"), null);

        assertEquals(new BigDecimal("320.00"), antes.getCosto());
        assertEquals(new BigDecimal("400.00"), despues.getCosto());
        assertNotEquals(antes.getFolio(), despues.getFolio());
    }

    @Test
    void registroFiltraPorTextoYEstado() {
        p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-001"), null);
        SolicitudCopia privada = p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-002"), CopiasPrueba.ine());

        assertEquals(2, p.solicitudService.listar(" ", null, 0, 10).getTotalElements());
        assertEquals(1, p.solicitudService.listar(null, EstadoSolicitud.EN_ESPERA_VALIDACION, 0, 10).getTotalElements());
        assertEquals(1, p.solicitudService.listar("exp-2026-002", null, 0, 10).getTotalElements());
        assertEquals(1, p.solicitudService.listar(privada.getFolio().toLowerCase(), null, 0, 10).getTotalElements());
        assertEquals(2, p.solicitudService.listar("castro", null, 0, 10).getTotalElements());
        // Un tamaño de página que no está en la lista usa el de 10
        assertEquals(10, p.solicitudService.listar(null, null, 0, 7).getSize());
        assertEquals(1L, p.solicitudService.totalesPorEstado().get(EstadoSolicitud.LISTO_PARA_PAGO));
        assertFalse(p.solicitudService.totalesPorEstado().isEmpty());
    }
}
