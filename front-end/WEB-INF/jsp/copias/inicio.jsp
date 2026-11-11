<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- Inicio del módulo (/copias): pendientes del día y últimas solicitudes --%>
<t:layout titulo="Inicio" modulo="copias" seccion="inicio">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>Inicio</n:migas>
        <n:encabezado titulo="Copias certificadas"
                      descripcion="Captura, validación, pago simulado y entrega de copias certificadas.">
            <a class="btn btn-primary" href="<c:url value='/copias/solicitudes/nueva'/>">+ Nueva solicitud</a>
        </n:encabezado>

        <div class="row g-3 mt-1 mb-3">
            <div class="col-md-4">
                <a class="sij-tarjeta p-4 cop-indicador" href="<c:url value='/copias/validacion'/>">
                    <div class="cop-numero"><c:out value="${porValidar}"/></div>
                    <div class="fw-semibold">Por validar</div>
                    <div class="sij-texto-chico">Solicitudes privadas en espera del visto bueno</div>
                </a>
            </div>
            <div class="col-md-4">
                <a class="sij-tarjeta p-4 cop-indicador" href="<c:url value='/copias/solicitudes?estado=LISTO_PARA_PAGO'/>">
                    <div class="cop-numero"><c:out value="${sinPagar}"/></div>
                    <div class="fw-semibold">Sin pagar</div>
                    <div class="sij-texto-chico">Listas para pago o aprobadas</div>
                </a>
            </div>
            <div class="col-md-4">
                <a class="sij-tarjeta p-4 cop-indicador" href="<c:url value='/copias/entregas'/>">
                    <div class="cop-numero"><c:out value="${porEntregar}"/></div>
                    <div class="fw-semibold">Por entregar</div>
                    <div class="sij-texto-chico">Pagadas en preparación o listas para recoger</div>
                </a>
            </div>
        </div>

        <div class="d-flex flex-wrap gap-2 mb-3">
            <c:forEach var="e" items="${estados}">
                <a class="sij-contador text-decoration-none" href="<c:url value='/copias/solicitudes?estado=${e}'/>">
                    &bull; <c:out value="${totales[e]}"/> <c:out value="${e.texto}"/>
                </a>
            </c:forEach>
        </div>

        <n:tarjeta titulo="Últimas solicitudes" sinRelleno="true">
            <c:choose>
                <c:when test="${empty recientes}">
                    <div class="p-4 text-center sij-descripcion">Aún no hay solicitudes registradas.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table sij-tabla align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Folio</th>
                                <th>Expediente</th>
                                <th>Solicitante</th>
                                <th>Estado</th>
                                <th class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="s" items="${recientes}">
                                <tr>
                                    <td>
                                        <div class="fw-semibold"><c:out value="${s.folio}"/></div>
                                        <div class="sij-texto-chico"><c:out value="${s.fechaSolicitudTexto}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.numeroExpediente}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.tipoTexto}"/></div>
                                    </td>
                                    <td><c:out value="${s.solicitanteNombre}"/></td>
                                    <td><n:estado valor="${s.estado.texto}" color="${s.estado.color}"/></td>
                                    <td class="text-end">
                                        <a class="btn btn-sm btn-sij-secundario" href="<c:url value='/copias/solicitudes/${s.id}'/>">Ver</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="p-3 text-end">
                        <a href="<c:url value='/copias/solicitudes'/>">Ver todo el registro (<c:out value="${totalSolicitudes}"/>)</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </n:tarjeta>
    </jsp:body>
</t:layout>
