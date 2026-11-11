<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%--
  RF 3.1 Visto bueno. Junto a los datos del solicitante se muestran las partes del expediente (ExpedienteApi)
  para compararlas. Las partes no se guardan en Copias. La INE solo se ve aquí.
--%>
<t:layout titulo="Validar ${solicitud.folio}" modulo="copias" seccion="validacion">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas><a href="<c:url value='/copias/validacion'/>">Validación de privadas</a> / <c:out value="${solicitud.folio}"/></n:migas>
        <n:encabezado titulo="Validar solicitud ${solicitud.folio}"
                      descripcion="Expediente privado ${solicitud.numeroExpediente} · ${solicitud.juzgado}">
            <n:estado valor="${solicitud.estado.texto}" color="${solicitud.estado.color}"/>
        </n:encabezado>

        <div class="row g-3 mt-1">
            <div class="col-lg-6">
                <n:tarjeta titulo="Solicitante">
                    <div class="cop-dato"><span>Nombre</span><strong><c:out value="${solicitud.solicitanteNombre}"/></strong></div>
                    <div class="cop-dato"><span>RFC</span><strong><c:out value="${solicitud.solicitanteRfc}"/></strong></div>
                    <div class="cop-dato"><span>Correo</span><strong><c:out value="${solicitud.solicitanteCorreo}"/></strong></div>
                    <div class="cop-dato"><span>Capturada</span><strong><c:out value="${solicitud.fechaSolicitudTexto}"/></strong></div>
                </n:tarjeta>

                <n:tarjeta titulo="Identificación oficial (INE)">
                    <c:url var="urlIne" value="/copias/validacion/${solicitud.id}/ine"/>
                    <c:choose>
                        <c:when test="${empty solicitud.ineArchivoId}">
                            <p class="sij-descripcion mb-0">La solicitud no tiene identificación.</p>
                        </c:when>
                        <c:otherwise>
                            <%-- PDF en un visor; imagen directamente --%>
                            <c:choose>
                                <c:when test="${ineEsPdf}">
                                    <iframe class="cop-ine-pdf" src="<c:out value='${urlIne}'/>" title="Identificación oficial del solicitante"></iframe>
                                </c:when>
                                <c:otherwise>
                                    <img class="cop-ine" src="<c:out value='${urlIne}'/>" alt="Identificación oficial del solicitante">
                                </c:otherwise>
                            </c:choose>
                            <div class="mt-2"><a href="<c:out value='${urlIne}'/>" target="_blank" rel="noopener">Abrir en otra pestaña</a></div>
                        </c:otherwise>
                    </c:choose>
                </n:tarjeta>
            </div>

            <div class="col-lg-6">
                <n:tarjeta titulo="Partes del expediente" sinRelleno="true">
                    <c:choose>
                        <c:when test="${empty expediente}">
                            <div class="p-4 sij-descripcion">No se pudo consultar el expediente en Oficialía.</div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table sij-tabla align-middle mb-0">
                                    <thead><tr><th>Nombre</th><th>RFC</th><th>Rol</th></tr></thead>
                                    <tbody>
                                    <c:forEach var="p" items="${expediente.partes()}">
                                        <c:set var="coincide" value="${fn:toUpperCase(p.rfc()) eq solicitud.solicitanteRfc}"/>
                                        <tr class="${coincide ? 'cop-coincide' : ''}">
                                            <td><c:out value="${p.nombre()}"/></td>
                                            <td><c:out value="${p.rfc()}"/></td>
                                            <td>
                                                <c:out value="${p.rol()}"/>
                                                <c:if test="${coincide}"><div class="sij-texto-chico fw-semibold">Coincide el RFC</div></c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="p-3 sij-texto-chico">
                                <c:choose>
                                    <c:when test="${not empty coincidencias}">El RFC del solicitante coincide con una parte del expediente. Revise también la INE.</c:when>
                                    <c:otherwise>El RFC del solicitante no coincide con ninguna parte. Revise con cuidado antes de aprobar.</c:otherwise>
                                </c:choose>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </n:tarjeta>

                <c:choose>
                    <c:when test="${enEspera}">
                        <n:tarjeta titulo="Decisión">
                            <p class="sij-descripcion">Aprobar habilita el pago. Rechazar cancela la solicitud y ya no se podrá pagar.</p>
                            <div class="d-flex justify-content-end mb-3">
                                <n:confirmar accion="/copias/validacion/${solicitud.id}/aprobar"
                                             texto="¿Aprobar la solicitud ${solicitud.folio}? Se habilitará el pago."
                                             boton="Aprobar" clase="btn btn-primary"/>
                            </div>
                            <form method="post" action="<c:url value='/copias/validacion/${solicitud.id}/rechazar'/>"
                                  data-confirmar="¿Rechazar la solicitud ${fn:escapeXml(solicitud.folio)}? Quedará Cancelada y no se podrá pagar."
                                  class="border-top pt-3">
                                <n:csrf/>
                                <label for="motivo" class="form-label">Motivo del rechazo *</label>
                                <textarea id="motivo" name="motivo" class="form-control" rows="3" required
                                          maxlength="<c:out value='${motivoMaximo}'/>"
                                          placeholder="Por ejemplo: el solicitante no es parte del expediente"></textarea>
                                <div class="d-flex justify-content-end mt-2">
                                    <button type="submit" class="btn btn-outline-danger">Rechazar</button>
                                </div>
                            </form>
                        </n:tarjeta>
                    </c:when>
                    <c:otherwise>
                        <div class="cop-aviso">
                            Esta solicitud ya fue validada (<c:out value="${solicitud.estado.texto}"/>).
                            <a href="<c:url value='/copias/solicitudes/${solicitud.id}'/>">Ver el detalle</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </jsp:body>
</t:layout>
