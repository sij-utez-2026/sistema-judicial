<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%--
  COPIAS_ADMIN: colores de las pantallas de Copias. Solo cambian este módulo; el diseño común del núcleo no se toca.
  Cada color tiene un selector y su campo de texto #RRGGBB (copias.js los mantiene iguales).
--%>
<t:layout titulo="Colores" modulo="copias" seccion="colores">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/recursos/copias/js/copias.js'/>"></script>
    </jsp:attribute>
    <jsp:body>
        <n:migas>Administración / Colores</n:migas>
        <n:encabezado titulo="Colores del área"
                      descripcion="Cambian solo las pantallas de Copias Certificadas. Los valores iniciales son la paleta institucional.">
            <n:confirmar accion="/copias/admin/colores/restablecer"
                         texto="¿Volver a la paleta institucional? Se perderán los colores actuales."
                         boton="Restablecer colores"/>
        </n:encabezado>

        <form:form method="post" modelAttribute="coloresForm" servletRelativeAction="/copias/admin/colores"
                   novalidate="novalidate" cssClass="mt-3">
            <n:tarjeta titulo="Paleta">
                <div class="row g-4">
                    <%-- campo · etiqueta · descripción · valor inicial --%>
                    <c:forEach var="campo" items="primario,titulos,botones,acento,bordes,avisos">
                        <c:choose>
                            <c:when test="${campo eq 'primario'}"><c:set var="etiqueta" value="Menú lateral (Bosque)"/><c:set var="inicial" value="${coloresIniciales.primario}"/><c:set var="valor" value="${coloresForm.primario}"/></c:when>
                            <c:when test="${campo eq 'titulos'}"><c:set var="etiqueta" value="Títulos (Tierra)"/><c:set var="inicial" value="${coloresIniciales.titulos}"/><c:set var="valor" value="${coloresForm.titulos}"/></c:when>
                            <c:when test="${campo eq 'botones'}"><c:set var="etiqueta" value="Botones principales (Bugambilia)"/><c:set var="inicial" value="${coloresIniciales.botones}"/><c:set var="valor" value="${coloresForm.botones}"/></c:when>
                            <c:when test="${campo eq 'acento'}"><c:set var="etiqueta" value="Detalles (Dorado)"/><c:set var="inicial" value="${coloresIniciales.acento}"/><c:set var="valor" value="${coloresForm.acento}"/></c:when>
                            <c:when test="${campo eq 'bordes'}"><c:set var="etiqueta" value="Bordes (Piedra)"/><c:set var="inicial" value="${coloresIniciales.bordes}"/><c:set var="valor" value="${coloresForm.bordes}"/></c:when>
                            <c:otherwise><c:set var="etiqueta" value="Avisos (Caña)"/><c:set var="inicial" value="${coloresIniciales.avisos}"/><c:set var="valor" value="${coloresForm.avisos}"/></c:otherwise>
                        </c:choose>
                        <div class="col-md-6">
                            <form:label path="${campo}" cssClass="form-label"><c:out value="${etiqueta}"/></form:label>
                            <div class="d-flex align-items-center gap-2">
                                <input type="color" class="form-control form-control-color" aria-label="Elegir color: <c:out value='${etiqueta}'/>"
                                       value="<c:out value='${valor}'/>" data-color="#<c:out value='${campo}'/>">
                                <form:input path="${campo}" maxlength="7" cssClass="form-control text-uppercase"
                                            cssErrorClass="form-control text-uppercase is-invalid"/>
                            </div>
                            <form:errors path="${campo}" cssClass="text-danger small" element="div"/>
                            <div class="form-text">Inicial: <c:out value="${inicial}"/></div>
                        </div>
                    </c:forEach>
                </div>
            </n:tarjeta>

            <div class="d-flex justify-content-end gap-2">
                <a class="btn btn-sij-secundario" href="<c:url value='/copias'/>">Cancelar</a>
                <button type="submit" class="btn btn-primary">Guardar colores</button>
            </div>
        </form:form>
    </jsp:body>
</t:layout>
