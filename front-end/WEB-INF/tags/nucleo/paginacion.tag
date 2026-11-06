<%@ tag description="Paginación de un Page de Spring, conservando los filtros" pageEncoding="UTF-8" %>
<%--
    <n:paginacion pagina="${pagina}" url="/peritajes/peritos"/>
  pagina : el Page<...> que devuelve el service.
  url    : la misma URL de la lista. Los filtros de la búsqueda actual (?q=...&estado=...) se conservan solos;
           solo cambia el parámetro "page" (empieza en 0).
  Escribe "Mostrando 1–25 de 80 registros" y los botones Anterior / números / Siguiente.
--%>
<%@ attribute name="pagina" required="true" type="org.springframework.data.domain.Page" %>
<%@ attribute name="url" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- URL base con los filtros actuales, sin "page" --%>
<c:url var="base" value="${url}">
    <c:forEach var="p" items="${paramValues}">
        <c:if test="${p.key ne 'page'}">
            <c:forEach var="v" items="${p.value}">
                <c:if test="${not empty v}"><c:param name="${p.key}" value="${v}"/></c:if>
            </c:forEach>
        </c:if>
    </c:forEach>
</c:url>
<c:set var="unir" value="${fn:contains(base, '?') ? '&' : '?'}"/>

<%-- Se muestran a lo más 5 números alrededor de la página actual --%>
<c:set var="desde" value="${pagina.number - 2 lt 0 ? 0 : pagina.number - 2}"/>
<c:set var="hasta" value="${pagina.number + 2 gt pagina.totalPages - 1 ? pagina.totalPages - 1 : pagina.number + 2}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
    <span class="sij-texto-chico">
        <c:choose>
            <c:when test="${pagina.totalElements eq 0}">Sin registros</c:when>
            <c:otherwise>
                Mostrando <c:out value="${pagina.number * pagina.size + 1}"/>–<c:out value="${pagina.number * pagina.size + pagina.numberOfElements}"/>
                de <c:out value="${pagina.totalElements}"/> registros
            </c:otherwise>
        </c:choose>
    </span>
    <c:if test="${pagina.totalPages gt 1}">
        <nav aria-label="Páginas">
            <ul class="pagination pagination-sm mb-0">
                <li class="page-item${pagina.first ? ' disabled' : ''}">
                    <a class="page-link" href="<c:out value='${base}${unir}page=${pagina.number - 1}'/>">Anterior</a>
                </li>
                <c:forEach var="i" begin="${desde}" end="${hasta}">
                    <li class="page-item${i eq pagina.number ? ' active' : ''}">
                        <a class="page-link" href="<c:out value='${base}${unir}page=${i}'/>"<c:if test="${i eq pagina.number}"> aria-current="page"</c:if>><c:out value="${i + 1}"/></a>
                    </li>
                </c:forEach>
                <li class="page-item${pagina.last ? ' disabled' : ''}">
                    <a class="page-link" href="<c:out value='${base}${unir}page=${pagina.number + 1}'/>">Siguiente</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>
