<%@ tag description="Etiqueta de estado con punto de color" pageEncoding="UTF-8" %>
<%--
    <n:estado valor="Activo"/>                    → punto verde
    <n:estado valor="${p.estado}"/>               → el color se elige por el texto (ver abajo)
    <n:estado valor="En revisión" color="azul"/>  → color fijo: verde, ambar, gris, rojo o azul

  Color automático según el texto (sin importar mayúsculas):
    rojo  : cancel, rechaz, improcedente, anulad
    gris  : inactiv, pendiente, borrador, archivad
    ambar : asignad, proceso, espera, programad, preparaci, reprogramad
    verde : todo lo demás (activo, entregado, resuelta, aprobado...)
--%>
<%@ attribute name="valor" required="true" %>
<%@ attribute name="color" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="texto" value="${fn:toLowerCase(valor)}"/>
<c:choose>
    <c:when test="${not empty color}"><c:set var="tono" value="${color}"/></c:when>
    <c:when test="${fn:contains(texto, 'cancel') or fn:contains(texto, 'rechaz') or fn:contains(texto, 'improcedente') or fn:contains(texto, 'anulad')}">
        <c:set var="tono" value="rojo"/>
    </c:when>
    <c:when test="${fn:contains(texto, 'inactiv') or fn:contains(texto, 'pendiente') or fn:contains(texto, 'borrador') or fn:contains(texto, 'archivad')}">
        <c:set var="tono" value="gris"/>
    </c:when>
    <c:when test="${fn:contains(texto, 'asignad') or fn:contains(texto, 'proceso') or fn:contains(texto, 'espera') or fn:contains(texto, 'programad') or fn:contains(texto, 'preparaci')}">
        <c:set var="tono" value="ambar"/>
    </c:when>
    <c:otherwise><c:set var="tono" value="verde"/></c:otherwise>
</c:choose>
<span class="sij-estado sij-estado-<c:out value='${tono}'/>"><c:out value="${valor}"/></span>
