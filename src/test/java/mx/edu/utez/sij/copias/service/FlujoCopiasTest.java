package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.model.TipoCorreo;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Validación (RF 3.1), pago simulado (RF 4.1) y entrega (RF 5.1). */
class FlujoCopiasTest {

    private final CopiasPrueba p = new CopiasPrueba();

    private SolicitudCopia privada() {
        return p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-002"), CopiasPrueba.ine());
    }

    private SolicitudCopia publica() {
        return p.solicitudService.registrar(CopiasPrueba.form("EXP-2026-001"), null);
    }

    @Test
    void privadaNoSePuedePagarAntesDelVistoBueno() {
        SolicitudCopia s = privada();
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> p.pagoService.pagar(s.getId()));
        assertEquals("Debe esperar la aprobación antes de pagar", e.getMessage());
    }

    @Test
    void aprobarHabilitaElPagoYSoloUnaVez() {
        SolicitudCopia s = privada();
        assertEquals(EstadoSolicitud.APROBADO, p.validacionService.aprobar(s.getId()).getEstado());
        assertThrows(ReglaNegocioException.class, () -> p.validacionService.aprobar(s.getId()));

        p.pagoService.pagar(s.getId());
        assertEquals(EstadoSolicitud.PAGADO_EN_PREPARACION, p.solicitudService.obtener(s.getId()).getEstado());
    }

    @Test
    void rechazarPideMotivoYBloqueaElPago() {
        SolicitudCopia s = privada();
        assertThrows(ReglaNegocioException.class, () -> p.validacionService.rechazar(s.getId(), "  "));

        SolicitudCopia rechazada = p.validacionService.rechazar(s.getId(), "No es parte del expediente");
        assertEquals(EstadoSolicitud.CANCELADO, rechazada.getEstado());
        assertEquals("No es parte del expediente", rechazada.getMotivoRechazo());
        assertThrows(ReglaNegocioException.class, () -> p.pagoService.pagar(s.getId()));
    }

    @Test
    void publicaNoPasaPorValidacion() {
        SolicitudCopia s = publica();
        assertThrows(ReglaNegocioException.class, () -> p.validacionService.aprobar(s.getId()));
        assertTrue(p.validacionService.pendientes().isEmpty());
    }

    @Test
    void elRfcDelSolicitanteSeComparaConLasPartes() {
        // El formulario de prueba usa el RFC de la demandante de EXP-2026-002
        assertEquals(1, p.validacionService.partesQueCoinciden(privada()).size());
    }

    @Test
    void pagoUnicoConComprobantePorCorreo() {
        SolicitudCopia s = publica();
        Pago pago = p.pagoService.pagar(s.getId());

        assertTrue(pago.isSimulado());
        assertEquals(s.getCosto(), pago.getMonto());
        assertTrue(pago.getFolioPago().matches("CP-\\d{4}-[A-Z2-9]{6}"));
        assertEquals(1, p.correo.enviados);
        assertEquals(TipoCorreo.COMPROBANTE, p.correoService.enviados(s.getId()).get(0).getTipo());
        assertThrows(ReglaNegocioException.class, () -> p.pagoService.pagar(s.getId()));

        p.pagoService.reenviarComprobante(s.getId());
        assertEquals(2, p.correo.enviados);
    }

    @Test
    void entregaAlMomentoInhabilitaElFolio() {
        SolicitudCopia s = publica();
        assertThrows(ReglaNegocioException.class, () -> p.entregaService.entregar(s.getId()));

        p.pagoService.pagar(s.getId());
        p.entregaService.entregar(s.getId());

        assertEquals(EstadoSolicitud.ENTREGADO, p.solicitudService.obtener(s.getId()).getEstado());
        assertTrue(p.entregaService.entregaDe(s.getId()).isPresent());
        assertThrows(ReglaNegocioException.class, () -> p.entregaService.entregar(s.getId()));
        assertThrows(ReglaNegocioException.class, () -> p.entregaService.avisarListaParaRecoger(s.getId()));
        assertTrue(p.entregaService.pendientes().isEmpty());
    }

    @Test
    void sinCopiaDisponibleSeAvisaPorCorreoYDespuesSeEntrega() {
        SolicitudCopia s = publica();
        p.pagoService.pagar(s.getId());
        assertEquals(1, p.entregaService.pendientes().size());

        p.entregaService.avisarListaParaRecoger(s.getId());
        assertEquals(EstadoSolicitud.LISTO_PARA_RECOGER, p.solicitudService.obtener(s.getId()).getEstado());
        // Se puede reenviar el aviso
        p.entregaService.avisarListaParaRecoger(s.getId());
        assertEquals(3, p.correo.enviados);

        p.entregaService.entregar(s.getId());
        assertEquals(EstadoSolicitud.ENTREGADO, p.solicitudService.obtener(s.getId()).getEstado());
        // Creación, pago, aviso y entrega (reenviar el aviso no cambia el estado)
        assertEquals(4, p.historialService.historial(s.getId()).size());
    }

    @Test
    void siFallaElCorreoDelAvisoNoCambiaElEstado() {
        SolicitudCopia s = publica();
        p.pagoService.pagar(s.getId());
        p.correo.falla = true;

        assertThrows(ReglaNegocioException.class, () -> p.entregaService.avisarListaParaRecoger(s.getId()));
        assertEquals(EstadoSolicitud.PAGADO_EN_PREPARACION, p.solicitudService.obtener(s.getId()).getEstado());
        assertFalse(p.correoService.enviados(s.getId()).get(0).isExito());
    }
}
