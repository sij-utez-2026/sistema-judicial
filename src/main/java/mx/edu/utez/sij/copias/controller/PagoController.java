package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.model.CorreoEnviado;
import mx.edu.utez.sij.copias.model.Pago;
import mx.edu.utez.sij.copias.model.SolicitudCopia;
import mx.edu.utez.sij.copias.service.CorreoCopiasService;
import mx.edu.utez.sij.copias.service.EntregaService;
import mx.edu.utez.sij.copias.service.PagoService;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Pago simulado (RF 4.1) y comprobante (imprimir o reenviar por correo). COPIAS_PERSONAL.
 *
 * TODO: @PreAuthorize("hasRole('COPIAS_PERSONAL')") cuando los integradores agreguen Spring Security.
 */
@Controller
@RequestMapping("/copias/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final SolicitudCopiaService solicitudService;
    private final EntregaService entregaService;
    private final CorreoCopiasService correoService;

    public PagoController(PagoService pagoService, SolicitudCopiaService solicitudService,
                          EntregaService entregaService, CorreoCopiasService correoService) {
        this.pagoService = pagoService;
        this.solicitudService = solicitudService;
        this.entregaService = entregaService;
        this.correoService = correoService;
    }

    // Resumen antes de pagar: expediente, costo congelado y la leyenda de pago simulado
    @GetMapping("/{id}")
    public String resumen(@PathVariable Long id, Model model) {
        SolicitudCopia solicitud = buscarONoEncontrada(id);
        if (pagoService.pagoDe(id).isPresent()) {
            return "redirect:/copias/pagos/" + id + "/comprobante";
        }
        try {
            pagoService.revisarQueSePuedePagar(solicitud);
            model.addAttribute("sePuedePagar", true);
        } catch (ReglaNegocioException e) {
            model.addAttribute("sePuedePagar", false);
            model.addAttribute("motivoNoPago", e.getMessage());
        }
        model.addAttribute("solicitud", solicitud);
        return "copias/pagos/pago";
    }

    @PostMapping("/{id}")
    public String pagar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Pago pago = pagoService.pagar(id);
            SolicitudCopia solicitud = solicitudService.obtener(id);
            flash.addFlashAttribute("exito", "Pago confirmado correctamente");
            flash.addFlashAttribute("exitoDetalle", "Comprobante " + pago.getFolioPago() + " enviado a "
                    + solicitud.getSolicitanteCorreo() + ".");
            return "redirect:/copias/pagos/" + id + "/comprobante";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/copias/pagos/" + id;
        }
    }

    // Comprobante imprimible. Datos esenciales, sin la INE
    @GetMapping("/{id}/comprobante")
    public String comprobante(@PathVariable Long id, Model model) {
        SolicitudCopia solicitud = buscarONoEncontrada(id);
        Optional<Pago> pago = pagoService.pagoDe(id);
        if (pago.isEmpty()) {
            return "redirect:/copias/pagos/" + id;
        }
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("pago", pago.get());
        model.addAttribute("entrega", entregaService.entregaDe(id).orElse(null));
        model.addAttribute("correos", correoService.enviados(id));
        return "copias/pagos/comprobante";
    }

    @PostMapping("/{id}/comprobante/enviar")
    public String reenviar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            CorreoEnviado correo = pagoService.reenviarComprobante(id);
            flash.addFlashAttribute("exito", "Comprobante enviado");
            flash.addFlashAttribute("exitoDetalle", "Se envió a " + correo.getDestinatario() + ".");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/copias/pagos/" + id + "/comprobante";
    }

    private SolicitudCopia buscarONoEncontrada(Long id) {
        return solicitudService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron registros"));
    }
}
