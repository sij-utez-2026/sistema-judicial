<%@ tag description="Opción del menú lateral" pageEncoding="UTF-8" %>
<%--
  Se usa en front-end/WEB-INF/jsp/<modulo>/menu.jsp:
    <n:menu-item clave="peritos" url="/peritajes/peritos" texto="Catálogo de peritos" icono="personas"/>

  clave : se marca activa cuando la pantalla usa <t:layout seccion="peritos">.
  url   : sin url se muestra desactivada con la leyenda "Pendiente" (pantallas que aún no existen).
  icono : nombre de <n:icono> (inicio, personas, carpeta, documento, calendario, balanza, grafica, campana, ...).
--%>
<%@ attribute name="clave" required="true" %>
<%@ attribute name="texto" required="true" %>
<%@ attribute name="url" required="false" %>
<%@ attribute name="icono" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<c:choose>
    <c:when test="${empty url}">
        <span class="nav-link disabled" title="Pendiente" aria-disabled="true">
            <n:icono nombre="${empty icono ? 'punto' : icono}"/>
            <c:out value="${texto}"/>
        </span>
    </c:when>
    <c:otherwise>
        <c:set var="activo" value="${clave eq requestScope.seccionActual}"/>
        <a class="nav-link${activo ? ' active' : ''}" href="<c:url value='${url}'/>"<c:if test="${activo}"> aria-current="page"</c:if>>
            <n:icono nombre="${empty icono ? 'punto' : icono}"/>
            <c:out value="${texto}"/>
        </a>
    </c:otherwise>
</c:choose>
