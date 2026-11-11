package mx.edu.utez.sij.copias;

import mx.edu.utez.sij.copias.dto.PersonalForm;
import mx.edu.utez.sij.copias.dto.SolicitudForm;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.service.EntregaService;
import mx.edu.utez.sij.copias.service.PagoService;
import mx.edu.utez.sij.copias.service.PersonalCopiasService;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.service.TarifaService;
import mx.edu.utez.sij.copias.temporal.SesionTemporal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de prueba del módulo (docs/ARQUITECTURA.md, sección 11). Solo se insertan si no hay tarifas.
 * Nombres ficticios y correos de example.com (dominio reservado para pruebas).
 * Se usan los services para que pasen por las mismas reglas que una captura normal.
 *
 * No se crea una solicitud privada de prueba porque necesita una INE: para probar la validación,
 * capture una solicitud de EXP-2026-002 o EXP-2026-004 y adjunte cualquier PDF, JPG o PNG.
 */
@Component
@Order(50)
public class DatosInicialesCopias implements CommandLineRunner {

    private final TarifaService tarifaService;
    private final PersonalCopiasService personalService;
    private final SolicitudCopiaService solicitudService;
    private final PagoService pagoService;
    private final EntregaService entregaService;
    private final SesionTemporal sesion;

    public DatosInicialesCopias(TarifaService tarifaService, PersonalCopiasService personalService,
                                SolicitudCopiaService solicitudService, PagoService pagoService,
                                EntregaService entregaService, SesionTemporal sesion) {
        this.tarifaService = tarifaService;
        this.personalService = personalService;
        this.solicitudService = solicitudService;
        this.pagoService = pagoService;
        this.entregaService = entregaService;
        this.sesion = sesion;
    }

    @Override
    public void run(String... args) {
        if (tarifaService.total() > 0) {
            return;
        }

        // Pendiente 17: tarifa fija de prueba
        tarifaService.registrar(new BigDecimal("320.00"), LocalDate.of(2026, 1, 1), sesion.usuarioId());

        trabajador("María Fernanda López Díaz", "personal1@example.com");
        trabajador("Carlos Alberto Núñez Vela", "personal2@example.com");

        // Pública recién capturada: lista para pago
        solicitud("EXP-2026-001", "Laura Méndez Ortiz", "MEOL850312AB1", "laura.mendez@example.com");

        // Pública pagada, la copia aún no está: queda en preparación
        SolicitudCopia enPreparacion = solicitud("EXP-2026-003", "Jorge Luis Vega Ramos", "VERJ791110TT6",
                "jorge.vega@example.com");
        pagoService.pagar(enPreparacion.getId());

        // Pública pagada y entregada al momento
        SolicitudCopia entregada = solicitud("EXP-2026-001", "Roberto Salas Méndez", "SAMR800101AB2",
                "roberto.salas@example.com");
        pagoService.pagar(entregada.getId());
        entregaService.entregar(entregada.getId());
    }

    private void trabajador(String nombre, String correo) {
        PersonalForm form = new PersonalForm();
        form.setNombre(nombre);
        form.setCorreo(correo);
        personalService.registrar(form);
    }

    private SolicitudCopia solicitud(String expediente, String nombre, String rfc, String correo) {
        SolicitudForm form = new SolicitudForm();
        form.setNumeroExpediente(expediente);
        form.setSolicitanteNombre(nombre);
        form.setSolicitanteRfc(rfc);
        form.setSolicitanteCorreo(correo);
        return solicitudService.registrar(form, null);
    }
}
