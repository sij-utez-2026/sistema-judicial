<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- Registro de solicitudes con su estado: en espera, sin pagar, en preparación, entregado... --%>
<t:layout titulo="Registro de solicitudes" modulo="copias" seccion="solicitudes">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>Registro de solicitudes</n:migas>
        <n:encabezado titulo="Registro de solicitudes"
                      descripcion="Todas las solicitudes de copias certificadas y su estado.">
            <a class="btn btn-primary" href="<c:url value='/copias/solicitudes/nueva'/>">+ Nueva solicitud</a>
        </n:encabezado>

        <div class="d-flex flex-wrap gap-2 my-3">
            <span class="sij-contador">&bull; <c:out value="${totalSolicitudes}"/> En total</span>
            <c:forEach var="e" items="${estados}">
                <c:if test="${totales[e] gt 0}">
                    <span class="sij-contador">&bull; <c:out value="${totales[e]}"/> <c:out value="${e.texto}"/></span>
                </c:if>
            </c:forEach>
        </div>

        <%-- Filtros: el estado se aplica al cambiarlo (data-autoenvio); el texto con Enter --%>
        <form class="row g-2 mb-2" method="get" action="<c:url value='/copias/solicitudes'/>">
            <input type="hidden" name="size" value="<c:out value='${size}'/>">
            <div class="col-lg-6 col-md-7">
                <div class="input-group">
                    <button type="submit" class="btn btn-sij-secundario" aria-label="Buscar"><n:icono nombre="buscar" tamano="14"/></button>
                    <input type="search" class="form-control" name="q" value="<c:out value='${q}'/>"
                           placeholder="Buscar por folio, expediente o nombre del solicitante" aria-label="Buscar">
                </div>
            </div>
            <div class="col-lg-3 col-md-5">
                <select class="form-select" name="estado" aria-label="Estado" data-autoenvio>
                    <option value="">Estado: Todos</option>
                    <c:forEach var="e" items="${estados}">
                        <option value="<c:out value='${e}'/>"${e eq estado ? ' selected' : ''}><c:out value="${e.texto}"/></option>
                    </c:forEach>
                </select>
            </div>
        </form>

        <c:if test="${hayFiltros}">
            <div class="sij-texto-chico mb-3">
                Filtros aplicados · <c:out value="${pagina.totalElements}"/>
                <c:out value="${pagina.totalElements eq 1 ? 'resultado' : 'resultados'}"/>
                · <a href="<c:url value='/copias/solicitudes'/>">Quitar filtros</a>
            </div>
        </c:if>

        <n:tarjeta sinRelleno="true">
            <c:choose>
                <c:when test="${empty pagina.content}">
                    <div class="p-4 text-center sij-descripcion">
                        <c:out value="${totalSolicitudes eq 0 ? 'Aún no hay solicitudes registradas.' : 'No se encontraron registros'}"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table sij-tabla align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Folio / Fecha</th>
                                <th>Expediente</th>
                                <th>Solicitante</th>
                                <th>Costo</th>
                                <th>Estado</th>
                                <th class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="s" items="${pagina.content}">
                                <tr>
                                    <td>
                                        <div class="fw-semibold"><c:out value="${s.folio}"/></div>
                                        <div class="sij-texto-chico"><c:out value="${s.fechaSolicitudTexto}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.numeroExpediente}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.tipoTexto}"/> · <c:out value="${s.materia}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.solicitanteNombre}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.solicitanteCorreo}"/></div>
                                    </td>
                                    <td><c:out value="${s.costoTexto}"/></td>
                                    <td><n:estado valor="${s.estado.texto}" color="${s.estado.color}"/></td>
                                    <td class="text-end text-nowrap">
                                        <a class="btn btn-sm btn-sij-secundario" href="<c:url value='/copias/solicitudes/${s.id}'/>"
                                           aria-label="Ver la solicitud <c:out value='${s.folio}'/>">Ver</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </n:tarjeta>

        <c:if test="${not empty pagina.content}">
            <div class="d-flex flex-wrap align-items-center gap-3">
                <div class="flex-grow-1"><n:paginacion pagina="${pagina}" url="/copias/solicitudes"/></div>
                <form method="get" action="<c:url value='/copias/solicitudes'/>" class="d-flex align-items-center gap-2">
                    <c:if test="${not empty q}"><input type="hidden" name="q" value="<c:out value='${q}'/>"></c:if>
                    <c:if test="${not empty estado}"><input type="hidden" name="estado" value="<c:out value='${estado}'/>"></c:if>
                    <label for="size" class="sij-texto-chico text-nowrap">Por página</label>
                    <select id="size" name="size" class="form-select form-select-sm" data-autoenvio>
                        <c:forEach var="t" items="${tamanosPagina}">
                            <option value="<c:out value='${t}'/>"${t eq size ? ' selected' : ''}><c:out value="${t}"/></option>
                        </c:forEach>
                    </select>
                </form>
            </div>
        </c:if>
    </jsp:body>
</t:layout>
