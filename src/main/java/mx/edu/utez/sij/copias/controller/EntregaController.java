package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
import org.springframework.web.bind.annotation.ModelAttribute;
import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.service.EntregaService;
import mx.edu.utez.sij.copias.service.PagoService;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entregas (RF 5.1): copias pagadas que falta entregar. Aviso por correo cuando la copia está lista
 * y registro de la entrega al imprimirla. COPIAS_PERSONAL.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_PERSONAL')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/entregas")
public class EntregaController {

    private final EntregaService entregaService;
    private final PagoService pagoService;
    private final SolicitudCopiaService solicitudService;
    private final AccesoTemporal acceso;

    public EntregaController(EntregaService entregaService, PagoService pagoService,
                             SolicitudCopiaService solicitudService, AccesoTemporal acceso) {
        this.entregaService = entregaService;
        this.pagoService = pagoService;
        this.solicitudService = solicitudService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloTrabajador() {
        acceso.exigirRol(RolesCopias.PERSONAL);
    }

    @GetMapping
    public String pendientes(Model model) {
        List<SolicitudCopia> pendientes = entregaService.pendientes();
        // Folio del comprobante de cada una, para buscarlo en ventanilla
        Map<Long, Pago> pagos = new LinkedHashMap<>();
        for (SolicitudCopia solicitud : pendientes) {
            pagoService.pagoDe(solicitud.getId()).ifPresent(p -> pagos.put(solicitud.getId(), p));
        }
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("pagos", pagos);
        return "copias/entregas/lista";
    }

    @PostMapping("/{id}/avisar")
    public String avisar(@PathVariable Long id, @RequestParam(defaultValue = "lista") String volver,
                         RedirectAttributes flash) {
        try {
            CorreoEnviado correo = entregaService.avisarListaParaRecoger(id);
            flash.addFlashAttribute("exito", "Aviso enviado");
            flash.addFlashAttribute("exitoDetalle", "Se avisó a " + correo.getDestinatario()
                    + " que su copia está lista para recoger.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return regresar(id, volver);
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable Long id, @RequestParam(defaultValue = "lista") String volver,
                           RedirectAttributes flash) {
        try {
            entregaService.entregar(id);
            SolicitudCopia solicitud = solicitudService.obtener(id);
            flash.addFlashAttribute("exito", "Entrega registrada correctamente");
            flash.addFlashAttribute("exitoDetalle", solicitud.getFolio() + " · El folio quedó inhabilitado.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return regresar(id, volver);
    }

    // Solo dos destinos posibles (no se redirige a una URL que llegue en la petición)
    private String regresar(Long id, String volver) {
        if ("comprobante".equals(volver)) {
            return "redirect:/copias/pagos/" + id + "/comprobante";
        }
        if ("detalle".equals(volver)) {
            return "redirect:/copias/solicitudes/" + id;
        }
        return "redirect:/copias/entregas";
    }
}
