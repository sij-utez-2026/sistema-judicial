package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.dto.SolicitudForm;
import mx.edu.utez.sij.copias.repository.memoria.ColorAreaRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.CorreoEnviadoRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.EntregaRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.HistorialSolicitudRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.PagoRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.SolicitudCopiaRepositoryMemoria;
import mx.edu.utez.sij.copias.repository.memoria.TarifaRepositoryMemoria;
import mx.edu.utez.sij.copias.temporal.ArchivoServiceTemporal;
import mx.edu.utez.sij.copias.temporal.CorreoTemporal;
import mx.edu.utez.sij.copias.temporal.ExpedienteApiTemporal;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import mx.edu.utez.sij.copias.temporal.UsuarioServiceTemporal;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Arma los services de Copias con los repositorios en memoria, sin Spring, para las pruebas.
 * correo.falla = true simula que el servidor de correo no responde.
 */
class CopiasPrueba {

    static class CorreoDePrueba extends CorreoTemporal {
        boolean falla;
        int enviados;

        @Override
        public boolean enviar(String destinatario, String asunto, String cuerpo) {
            if (falla) {
                return false;
            }
            enviados++;
            return true;
        }
    }

    final SesionTemporal sesion = new SesionTemporal();
    final SolicitudCopiaRepositoryMemoria solicitudes = new SolicitudCopiaRepositoryMemoria();
    final PagoRepositoryMemoria pagos = new PagoRepositoryMemoria();
    final CorreoEnviadoRepositoryMemoria correos = new CorreoEnviadoRepositoryMemoria();
    final CorreoDePrueba correo = new CorreoDePrueba();

    final TarifaService tarifaService = new TarifaService(new TarifaRepositoryMemoria());
    final FolioService folioService = new FolioService(solicitudes, pagos);
    final HistorialService historialService =
            new HistorialService(solicitudes, new HistorialSolicitudRepositoryMemoria(), sesion);
    final ExpedienteApiTemporal expedienteApi = new ExpedienteApiTemporal();
    final ArchivoServiceTemporal archivoService = new ArchivoServiceTemporal();
    final SolicitudCopiaService solicitudService = new SolicitudCopiaService(solicitudes, expedienteApi,
            archivoService, tarifaService, folioService, historialService, sesion);
    final ValidacionService validacionService = new ValidacionService(solicitudes, solicitudService,
            historialService, expedienteApi, archivoService, sesion);
    final CorreoCopiasService correoService = new CorreoCopiasService(correo, correos, sesion);
    final PagoService pagoService = new PagoService(pagos, solicitudService, historialService, folioService,
            correoService, sesion);
    final EntregaService entregaService = new EntregaService(new EntregaRepositoryMemoria(), solicitudes,
            solicitudService, historialService, correoService, sesion);
    final PersonalCopiasService personalService = new PersonalCopiasService(new UsuarioServiceTemporal());
    final ColoresCopiasService coloresService = new ColoresCopiasService(new ColorAreaRepositoryMemoria(), sesion);

    CopiasPrueba() {
        tarifaService.registrar(new BigDecimal("320.00"), LocalDate.of(2026, 1, 1), 1L);
    }

    // EXP-2026-001 y 003 son públicos; EXP-2026-002 y 004 privados (ExpedienteApiTemporal)
    static SolicitudForm form(String expediente) {
        SolicitudForm form = new SolicitudForm();
        form.setNumeroExpediente(expediente);
        form.setSolicitanteNombre("  Andrea   Castro Ruiz ");
        form.setSolicitanteRfc(" cara900721mz4 ");
        form.setSolicitanteCorreo(" Andrea@Example.com ");
        return form;
    }

    static MockMultipartFile ine() {
        return new MockMultipartFile("ine", "ine.png", "image/png", new byte[]{1, 2, 3});
    }
}
