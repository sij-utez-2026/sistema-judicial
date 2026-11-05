<%@ tag description="Botón que envía un POST después de confirmar" pageEncoding="UTF-8" %>
<%--
    <n:confirmar accion="/peritajes/casos/5/cancelar" texto="¿Cancelar el peritaje PER-2026-00005?"
                 boton="Cancelar peritaje" clase="btn btn-outline-danger">
        <input type="hidden" name="motivo" value="...">   (campos extra, opcional)
    </n:confirmar>
  Muestra la ventana de confirmación del sistema; si se acepta, envía el formulario (con token CSRF).
--%>
<%@ attribute name="accion" required="true" %>
<%@ attribute name="texto" required="true" %>
<%@ attribute name="boton" required="false" %>
<%@ attribute name="clase" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<form method="post" action="<c:url value='${accion}'/>" class="d-inline" data-confirmar="<c:out value='${texto}'/>">
    <n:csrf/>
    <jsp:doBody/>
    <button type="submit" class="<c:out value="${empty clase ? 'btn btn-sij-secundario' : clase}"/>"><c:out value="${empty boton ? 'Confirmar' : boton}"/></button>
</form>
