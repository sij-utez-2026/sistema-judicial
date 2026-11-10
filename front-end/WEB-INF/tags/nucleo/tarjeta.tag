<%@ tag description="Recuadro blanco con título opcional" pageEncoding="UTF-8" %>
<%--
    <n:tarjeta titulo="Datos de identificación"> ... campos ... </n:tarjeta>
  clase: clases extra de Bootstrap (por ejemplo "h-100"). Para una tabla de borde a borde usar sinRelleno="true".
--%>
<%@ attribute name="titulo" required="false" %>
<%@ attribute name="clase" required="false" %>
<%@ attribute name="sinRelleno" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<section class="sij-tarjeta mb-3 ${sinRelleno ? '' : 'p-4'} <c:out value='${clase}'/>">
    <c:if test="${not empty titulo}">
        <h2 class="sij-tarjeta-titulo mb-3${sinRelleno ? ' px-4 pt-4' : ''}"><c:out value="${titulo}"/></h2>
    </c:if>
    <jsp:doBody/>
</section>
