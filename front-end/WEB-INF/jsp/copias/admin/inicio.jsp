<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- Inicio del administrador del área (COPIAS_ADMIN): trabajadores y colores --%>
<t:layout titulo="Administración" modulo="copias" seccion="admin-inicio">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>Administración</n:migas>
        <n:encabezado titulo="Administración del área"
                      descripcion="Trabajadores de Copias Certificadas y colores de las pantallas del módulo.">
            <a class="btn btn-primary" href="<c:url value='/copias/admin/personal'/>">+ Agregar trabajador</a>
        </n:encabezado>

        <div class="row g-3 mt-1">
            <div class="col-md-6">
                <a class="sij-tarjeta p-4 cop-indicador" href="<c:url value='/copias/admin/personal'/>">
                    <div class="cop-numero"><c:out value="${trabajadoresActivos}"/></div>
                    <div class="fw-semibold">Trabajadores activos</div>
                    <div class="sij-texto-chico">
                        <c:out value="${totalTrabajadores}"/> registrados · <c:out value="${trabajadoresInactivos}"/> inactivos.
                        Capturan, validan, cobran y entregan las copias.
                    </div>
                </a>
            </div>
            <div class="col-md-6">
                <a class="sij-tarjeta p-4 cop-indicador" href="<c:url value='/copias/admin/colores'/>">
                    <div class="d-flex gap-2 mb-2">
                        <c:forEach var="color" items="${coloresCopias}">
                            <span class="cop-muestra" style="background: <c:out value='${color.value}'/>"
                                  title="<c:out value='${color.value}'/>"></span>
                        </c:forEach>
                    </div>
                    <div class="fw-semibold">Colores del área</div>
                    <div class="sij-texto-chico">
                        <c:out value="${coloresPersonalizados ? 'Colores personalizados.' : 'Paleta institucional.'}"/>
                        Solo cambian las pantallas de Copias.
                    </div>
                </a>
            </div>
        </div>
    </jsp:body>
</t:layout>
