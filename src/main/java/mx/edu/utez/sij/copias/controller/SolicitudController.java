package mx.edu.utez.sij.copias.controller;

import jakarta.validation.Valid;
import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.dto.SolicitudForm;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.service.CorreoCopiasService;
import mx.edu.utez.sij.copias.service.EntregaService;
import mx.edu.utez.sij.copias.service.HistorialService;
import mx.edu.utez.sij.copias.service.PagoService;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.temporal.ExpedienteApi.ExpedienteDto;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Captura (RF 2.1 y 2.2), registro y detalle de solicitudes (RF 2.3). COPIAS_PERSONAL.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_PERSONAL')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/solicitudes")
public class SolicitudController {

    private final SolicitudCopiaService solicitudService;
    private final HistorialService historialService;
    private final PagoService pagoService;
    private final EntregaService entregaService;
    private final CorreoCopiasService correoService;
    private final AccesoTemporal acceso;

    public SolicitudController(SolicitudCopiaService solicitudService, HistorialService historialService,
                               PagoService pagoService, EntregaService entregaService,
                               CorreoCopiasService correoService, AccesoTemporal acceso) {
        this.solicitudService = solicitudService;
        this.historialService = historialService;
        this.pagoService = pagoService;
        this.entregaService = entregaService;
        this.correoService = correoService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloTrabajador() {
        acceso.exigirRol(RolesCopias.PERSONAL);
    }

    // Registro de solicitudes con su estado (en espera, sin pagar, entregado...)
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) EstadoSolicitud estado,
                         Model model) {
        Page<SolicitudCopia> pagina = solicitudService.listar(q, estado, page, size);
        model.addAttribute("pagina", pagina);
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute("totales", solicitudService.totalesPorEstado());
        model.addAttribute("totalSolicitudes", solicitudService.total());
        model.addAttribute("size", pagina.getSize());
        model.addAttribute("tamanosPagina", SolicitudCopiaService.TAMANOS_PAGINA);
        model.addAttribute("hayFiltros", (q != null && !q.isBlank()) || estado != null);
        return "copias/solicitudes/lista";
    }

    // Paso 1: buscar el expediente. Paso 2 (con ?expediente=...): capturar al solicitante
    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) String expediente, Model model) {
        SolicitudForm form = new SolicitudForm();
        form.setNumeroExpediente(expediente);
        model.addAttribute("solicitudForm", form);
        if (expediente != null && !expediente.isBlank()) {
            try {
                model.addAttribute("expediente", solicitudService.buscarExpediente(expediente));
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return mostrarCaptura(model);
    }

    @PostMapping("/nueva")
    public String registrar(@Valid @ModelAttribute("solicitudForm") SolicitudForm form,
                            BindingResult resultado,
                            @RequestParam(value = "ine", required = false) MultipartFile ine,
                            Model model,
                            RedirectAttributes flash) {
        ExpedienteDto expediente;
        try {
            expediente = solicitudService.buscarExpediente(form.getNumeroExpediente());
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarCaptura(model);
        }
        model.addAttribute("expediente", expediente);
        if (resultado.hasErrors()) {
            return mostrarCaptura(model);
        }
        try {
            SolicitudCopia solicitud = solicitudService.registrar(form, ine);
            if (solicitud.isPrivada()) {
                flash.addFlashAttribute("exito", "Solicitud registrada");
                flash.addFlashAttribute("exitoDetalle", solicitud.getFolio() + " · Debe esperar la aprobación antes de pagar.");
                return "redirect:/copias/solicitudes/" + solicitud.getId();
            }
            flash.addFlashAttribute("exito", "Solicitud registrada");
            flash.addFlashAttribute("exitoDetalle", solicitud.getFolio() + " · Expediente público: continúe con el pago.");
            return "redirect:/copias/pagos/" + solicitud.getId();
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarCaptura(model);
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        SolicitudCopia solicitud = solicitudService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron registros"));
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("historial", historialService.historial(id));
        model.addAttribute("pago", pagoService.pagoDe(id).orElse(null));
        model.addAttribute("entrega", entregaService.entregaDe(id).orElse(null));
        model.addAttribute("correos", correoService.enviados(id));
        return "copias/solicitudes/detalle";
    }

    private String mostrarCaptura(Model model) {
        model.addAttribute("tiposIne", ".pdf,.jpg,.jpeg,.png");
        model.addAttribute("maxMbIne", SolicitudCopiaService.TAMANO_MAXIMO_INE_MB);
        return "copias/solicitudes/nueva";
    }
}
