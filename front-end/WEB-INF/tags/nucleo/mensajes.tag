<%@ tag description="Mensajes de éxito y error (ya va dentro del layout)" pageEncoding="UTF-8" %>
<%--
  Muestra los mensajes que manda el controller:
    flash.addFlashAttribute("exito", "Registro exitoso");            → aviso verde arriba a la derecha (6 s)
    flash.addFlashAttribute("exitoDetalle", "Juan · PER-0001 ...");  → segunda línea del aviso (opcional)
    flash.addFlashAttribute("error", e.getMessage());                → alerta roja arriba del contenido
  (también sirven con model.addAttribute cuando no hay redirect).
  Los textos son los de los criterios de aceptación del DFR.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty error}">
    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
</c:if>
<c:if test="${not empty exito}">
    <div class="toast-container position-fixed top-0 end-0 p-3">
        <div class="toast sij-aviso border-success" role="status" aria-live="polite" aria-atomic="true" data-sij-aviso>
            <div class="d-flex p-3 gap-2">
                <span class="text-success fw-bold" aria-hidden="true">&#10003;</span>
                <div class="flex-grow-1">
                    <div class="fw-semibold"><c:out value="${exito}"/></div>
                    <c:if test="${not empty exitoDetalle}">
                        <div class="sij-texto-chico"><c:out value="${exitoDetalle}"/></div>
                    </c:if>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Cerrar"></button>
            </div>
        </div>
    </div>
</c:if>
