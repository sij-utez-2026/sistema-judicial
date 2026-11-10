<%@ tag description="Migas de pan y fecha, arriba de cada pantalla" pageEncoding="UTF-8" %>
<%--
    <n:migas><a href="<c:url value='/peritajes/peritos'/>">Catálogo de peritos</a> / Registrar perito</n:migas>
  Escribe "SGPJ / ..." con la sigla del módulo (enlace a su inicio) y la fecha y hora a la derecha.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="d-flex flex-wrap justify-content-between gap-2 sij-migas">
    <nav aria-label="Ubicación">
        <c:choose>
            <c:when test="${not empty requestScope.moduloActual}">
                <a href="<c:url value='/${requestScope.moduloActual.prefijo}'/>"><c:out value="${requestScope.moduloActual.sigla}"/></a>
            </c:when>
            <c:otherwise><a href="<c:url value='/'/>">Inicio</a></c:otherwise>
        </c:choose>
        / <jsp:doBody/>
    </nav>
    <span class="d-md-none"><c:out value="${fechaHoraActual}"/></span>
</div>
