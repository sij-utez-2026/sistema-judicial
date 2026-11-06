<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- RF 3.1: bandeja de solicitudes privadas en espera del visto bueno --%>
<t:layout titulo="Validación de privadas" modulo="copias" seccion="validacion">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>Validación de privadas</n:migas>
        <n:encabezado titulo="Validación de solicitudes privadas"
                      descripcion="Compare los datos y la INE del solicitante con las partes del expediente antes de aprobar."/>

        <n:tarjeta sinRelleno="true" clase="mt-3">
            <c:choose>
                <c:when test="${empty pendientes}">
                    <div class="p-4 text-center sij-descripcion">No hay solicitudes en espera de validación.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table sij-tabla align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Folio / Fecha</th>
                                <th>Expediente</th>
                                <th>Solicitante</th>
                                <th>Estado</th>
                                <th class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="s" items="${pendientes}">
                                <tr>
                                    <td>
                                        <div class="fw-semibold"><c:out value="${s.folio}"/></div>
                                        <div class="sij-texto-chico"><c:out value="${s.fechaSolicitudTexto}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.numeroExpediente}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.juzgado}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.solicitanteNombre}"/>
                                        <div class="sij-texto-chico">RFC <c:out value="${s.solicitanteRfc}"/></div>
                                    </td>
                                    <td><n:estado valor="${s.estado.texto}" color="${s.estado.color}"/></td>
                                    <td class="text-end">
                                        <a class="btn btn-sm btn-primary" href="<c:url value='/copias/validacion/${s.id}'/>">Revisar</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </n:tarjeta>
    </jsp:body>
</t:layout>
