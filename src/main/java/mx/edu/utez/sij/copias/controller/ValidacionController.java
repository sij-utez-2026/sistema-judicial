package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.RolesCopias;
import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.temporal.AccesoTemporal;
import org.springframework.web.bind.annotation.ModelAttribute;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.service.ValidacionService;
import mx.edu.utez.sij.copias.temporal.ArchivoServiceTemporal.Archivo;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Visto bueno de solicitudes privadas (RF 3.1). Solo COPIAS_PERSONAL ve la INE.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_PERSONAL')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/validacion")
public class ValidacionController {

    private final ValidacionService validacionService;
    private final SolicitudCopiaService solicitudService;
    private final AccesoTemporal acceso;

    public ValidacionController(ValidacionService validacionService, SolicitudCopiaService solicitudService,
                                AccesoTemporal acceso) {
        this.validacionService = validacionService;
        this.solicitudService = solicitudService;
        this.acceso = acceso;
    }

    // TEMPORAL: se cambia por @PreAuthorize en la clase cuando exista Spring Security
    @ModelAttribute
    public void soloTrabajador() {
        acceso.exigirRol(RolesCopias.PERSONAL);
    }

    @GetMapping
    public String bandeja(Model model) {
        model.addAttribute("pendientes", validacionService.pendientes());
        return "copias/validacion/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        SolicitudCopia solicitud = buscarONoEncontrada(id);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("expediente", validacionService.expediente(solicitud).orElse(null));
        model.addAttribute("coincidencias", validacionService.partesQueCoinciden(solicitud));
        model.addAttribute("enEspera", solicitud.getEstado() == EstadoSolicitud.EN_ESPERA_VALIDACION);
        model.addAttribute("motivoMaximo", ValidacionService.MOTIVO_MAXIMO);
        if (solicitud.getIneArchivoId() != null) {
            try {
                model.addAttribute("ineEsPdf", MediaType.APPLICATION_PDF.equals(
                        tipoPorExtension(validacionService.ine(id).nombreOriginal())));
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "copias/validacion/detalle";
    }

    // La INE se muestra dentro de la página (inline) y nunca tiene una URL pública
    @GetMapping("/{id}/ine")
    public ResponseEntity<byte[]> ine(@PathVariable Long id) {
        buscarONoEncontrada(id);
        Archivo archivo;
        try {
            archivo = validacionService.ine(id);
        } catch (ReglaNegocioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        HttpHeaders encabezados = new HttpHeaders();
        // El tipo se decide por la extensión ya validada, no por lo que mandó el navegador
        encabezados.setContentType(tipoPorExtension(archivo.nombreOriginal()));
        encabezados.setContentDisposition(ContentDisposition.inline()
                .filename(archivo.nombreOriginal(), StandardCharsets.UTF_8).build());
        encabezados.set("X-Content-Type-Options", "nosniff");
        encabezados.setCacheControl("no-store");
        return new ResponseEntity<>(archivo.contenido(), encabezados, HttpStatus.OK);
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            SolicitudCopia solicitud = validacionService.aprobar(id);
            flash.addFlashAttribute("exito", "Solicitud aprobada");
            flash.addFlashAttribute("exitoDetalle", solicitud.getFolio() + " · Ya se puede realizar el pago.");
            return "redirect:/copias/pagos/" + id;
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/copias/validacion/" + id;
        }
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                           RedirectAttributes flash) {
        try {
            SolicitudCopia solicitud = validacionService.rechazar(id, motivo);
            flash.addFlashAttribute("exito", "Solicitud rechazada");
            flash.addFlashAttribute("exitoDetalle", solicitud.getFolio() + " quedó Cancelada y no se podrá pagar.");
            return "redirect:/copias/validacion";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/copias/validacion/" + id;
        }
    }

    private SolicitudCopia buscarONoEncontrada(Long id) {
        SolicitudCopia solicitud = solicitudService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron registros"));
        if (!solicitud.isPrivada()) {
            // Las públicas no pasan por validación
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron registros");
        }
        return solicitud;
    }

    private MediaType tipoPorExtension(String nombre) {
        String minusculas = nombre.toLowerCase(Locale.ROOT);
        if (minusculas.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        if (minusculas.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.IMAGE_JPEG;
    }
}
