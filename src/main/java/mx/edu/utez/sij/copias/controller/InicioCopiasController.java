package mx.edu.utez.sij.copias.controller;

import mx.edu.utez.sij.copias.model.EstadoSolicitud;
import mx.edu.utez.sij.copias.service.EntregaService;
import mx.edu.utez.sij.copias.service.SolicitudCopiaService;
import mx.edu.utez.sij.copias.service.ValidacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Inicio del módulo (/copias): resumen de solicitudes por estado y accesos rápidos.
 * Vista: front-end/WEB-INF/jsp/copias/inicio.jsp
 *
 * TODO: @PreAuthorize("hasAnyRole('COPIAS_PERSONAL','COPIAS_ADMIN')") cuando los integradores agreguen Spring Security.
 */
@Controller
public class InicioCopiasController {

    private final SolicitudCopiaService solicitudService;
    private final ValidacionService validacionService;
    private final EntregaService entregaService;

    public InicioCopiasController(SolicitudCopiaService solicitudService, ValidacionService validacionService,
                                  EntregaService entregaService) {
        this.solicitudService = solicitudService;
        this.validacionService = validacionService;
        this.entregaService = entregaService;
    }

    @GetMapping("/copias")
    public String inicio(Model model) {
        Map<EstadoSolicitud, Long> totales = solicitudService.totalesPorEstado();
        model.addAttribute("totales", totales);
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute("totalSolicitudes", solicitudService.total());
        model.addAttribute("porValidar", validacionService.pendientes().size());
        model.addAttribute("porEntregar", entregaService.pendientes().size());
        model.addAttribute("sinPagar", totales.get(EstadoSolicitud.LISTO_PARA_PAGO) + totales.get(EstadoSolicitud.APROBADO));
        model.addAttribute("recientes", solicitudService.listar(null, null, 0, 10).getContent());
        return "copias/inicio";
    }
}
