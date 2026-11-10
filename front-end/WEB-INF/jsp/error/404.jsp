<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%-- Página de error 404 (la elige FrontEndConfig.vistasDeError). INTEGRADORES. --%>
<t:layout-publico titulo="Página no encontrada">
    <n:tarjeta clase="text-center mx-auto mt-4" >
        <div class="display-6 fw-semibold text-secondary"><c:out value="${empty status ? '404' : status}"/></div>
        <h1 class="sij-titulo mt-2">Página no encontrada</h1>
        <p class="sij-descripcion">La dirección no existe o el registro que busca ya no está disponible.</p>
        <c:if test="${not empty path}">
            <p class="sij-texto-chico">Dirección: <c:out value="${path}"/></p>
        </c:if>
        <a class="btn btn-primary" href="<c:url value='/'/>">Ir al inicio</a>
    </n:tarjeta>
</t:layout-publico>
