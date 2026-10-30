package mx.edu.utez.sij.peritajes.controller;

import jakarta.validation.Valid;
import mx.edu.utez.sij.peritajes.dto.CargaPerito;
import mx.edu.utez.sij.peritajes.dto.PeritoForm;
import mx.edu.utez.sij.peritajes.model.Perito;
import mx.edu.utez.sij.peritajes.service.PeritoService;
import mx.edu.utez.sij.peritajes.service.ReglaNegocioException;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catálogo de peritos (RF 3.1 – 3.4). Solo para PERITAJES_ADMIN y PERITAJES_PERITO_ADMIN.
 * Rutas de docs/modulos/peritajes.md, sección 8 (sin la vista de detalle: la carga y el estado están en Editar).
 *
 * TODO: agregar @PreAuthorize("hasAnyRole('PERITAJES_ADMIN','PERITAJES_PERITO_ADMIN')")
 * cuando los integradores agreguen Spring Security al proyecto.
 */
@Controller
@RequestMapping("/peritajes/peritos")
public class PeritoController {

    private final PeritoService peritoService;

    public PeritoController(PeritoService peritoService) {
        this.peritoService = peritoService;
    }

    // RF 3.2 Catálogo de peritos
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "25") int size,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) String especialidad,
                         @RequestParam(required = false) Long materiaId,
                         @RequestParam(required = false) String estado,
                         Model model) {
        // estado: "activo", "inactivo" o vacío (todos)
        Boolean activo = null;
        if ("activo".equals(estado)) {
            activo = true;
        } else if ("inactivo".equals(estado)) {
            activo = false;
        }

        Page<Perito> pagina = peritoService.listar(q, especialidad, materiaId, activo, page, size);

        // Materias y carga de cada perito de la página
        Map<Long, String> materiasPorPerito = new LinkedHashMap<>();
        Map<Long, CargaPerito> cargaPorPerito = new LinkedHashMap<>();
        for (Perito perito : pagina.getContent()) {
            materiasPorPerito.put(perito.getId(), peritoService.nombresMaterias(perito));
            cargaPorPerito.put(perito.getId(), peritoService.carga(perito.getId()));
        }

        model.addAttribute("pagina", pagina);
        model.addAttribute("materiasPorPerito", materiasPorPerito);
        model.addAttribute("cargaPorPerito", cargaPorPerito);
        model.addAttribute("totalRegistrados", peritoService.totalRegistrados());
        model.addAttribute("totalActivos", peritoService.totalActivos());
        model.addAttribute("totalInactivos", peritoService.totalInactivos());
        model.addAttribute("totalesPorMateria", peritoService.totalesPorMateria());
        model.addAttribute("especialidades", peritoService.especialidadesRegistradas());
        model.addAttribute("tamanosPagina", PeritoService.TAMANOS_PAGINA);
        model.addAttribute("materias", peritoService.materiasActivas());
        // Filtros actuales, para mantenerlos en el formulario y en la paginación
        model.addAttribute("q", q);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("materiaId", materiaId);
        model.addAttribute("estado", estado);
        model.addAttribute("size", pagina.getSize());
        model.addAttribute("hayFiltros", (q != null && !q.isBlank()) || (especialidad != null && !especialidad.isBlank())
                || materiaId != null || activo != null);
        return "peritajes/peritos/lista";
    }

    // RF 3.1 Registro de perito
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("peritoForm", new PeritoForm());
        model.addAttribute("materias", peritoService.materiasActivas());
        return "peritajes/peritos/registro";
    }

    @PostMapping("/nuevo")
    public String registrar(@Valid @ModelAttribute("peritoForm") PeritoForm form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                Perito perito = peritoService.registrar(form);
                // RF 3.1: confirmación de registro exitoso, en estado Activo
                flash.addFlashAttribute("exito", "Registro exitoso");
                flash.addFlashAttribute("exitoDetalle", perito.getNombreCompleto() + " · "
                        + perito.getNumeroRegistro() + " quedó registrado en estado Activo.");
                return "redirect:/peritajes/peritos";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("materias", peritoService.materiasActivas());
        return "peritajes/peritos/registro";
    }

    // RF 3.3 Edición de perito
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Perito perito = buscarONoEncontrado(id);
        model.addAttribute("peritoForm", peritoService.formularioDe(perito));
        return mostrarEdicion(perito, model);
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("peritoForm") PeritoForm form,
                             BindingResult resultado,
                             Model model,
                             RedirectAttributes flash) {
        Perito perito = buscarONoEncontrado(id);
        if (!resultado.hasErrors()) {
            try {
                Perito actualizado = peritoService.actualizar(id, form);
                flash.addFlashAttribute("exito", "Datos actualizados correctamente");
                flash.addFlashAttribute("exitoDetalle", actualizado.getNombreCompleto() + " · "
                        + actualizado.getNumeroRegistro() + " actualizado. Su estado permanece "
                        + (actualizado.isActivo() ? "Activo." : "Inactivo."));
                return "redirect:/peritajes/peritos";
            } catch (ReglaNegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return mostrarEdicion(perito, model);
    }

    // RF 3.4 Baja o reactivación
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam boolean activo,
                                RedirectAttributes flash) {
        buscarONoEncontrado(id);
        try {
            Perito perito = peritoService.cambiarEstado(id, activo);
            // RF 3.4: confirmación de baja o reactivación
            flash.addFlashAttribute("exito", perito.isActivo() ? "Perito reactivado" : "Perito dado de baja");
            flash.addFlashAttribute("exitoDetalle", perito.getNombreCompleto() + " · " + perito.getNumeroRegistro()
                    + (perito.isActivo() ? " vuelve a participar en la asignación automática."
                                         : " quedó Inactivo y ya no recibe asignaciones."));
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorEstado", e.getMessage());
        }
        return "redirect:/peritajes/peritos/" + id + "/editar";
    }

    private String mostrarEdicion(Perito perito, Model model) {
        model.addAttribute("perito", perito);
        model.addAttribute("materias", peritoService.materiasActivas());
        model.addAttribute("carga", peritoService.carga(perito.getId()));
        model.addAttribute("peritajesActivos", peritoService.peritajesActivos(perito.getId()));
        return "peritajes/peritos/editar";
    }

    private Perito buscarONoEncontrado(Long id) {
        return peritoService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El perito no existe"));
    }
}
