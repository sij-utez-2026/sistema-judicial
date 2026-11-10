<%@ tag description="Título y descripción de la pantalla, con botones a la derecha" pageEncoding="UTF-8" %>
<%--
    <n:encabezado titulo="Catálogo de peritos" descripcion="Padrón institucional...">
        <a class="btn btn-primary" href="<c:url value='/peritajes/peritos/nuevo'/>">+ Registrar perito</a>
    </n:encabezado>
  El contenido (opcional) son los botones de acción que van a la derecha del título.
--%>
<%@ attribute name="titulo" required="true" %>
<%@ attribute name="descripcion" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
    <div>
        <h1 class="sij-titulo"><c:out value="${titulo}"/></h1>
        <c:if test="${not empty descripcion}">
            <p class="sij-descripcion mb-0"><c:out value="${descripcion}"/></p>
        </c:if>
    </div>
    <div class="d-flex flex-wrap gap-2 mt-sm-3">
        <jsp:doBody/>
    </div>
</div>
