<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- Detalle de una solicitud: datos, siguiente paso según su estado e historial --%>
<t:layout titulo="Solicitud ${solicitud.folio}" modulo="copias" seccion="solicitudes">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas><a href="<c:url value='/copias/solicitudes'/>">Registro de solicitudes</a> / <c:out value="${solicitud.folio}"/></n:migas>
        <n:encabezado titulo="Solicitud ${solicitud.folio}"
                      descripcion="Expediente ${solicitud.numeroExpediente} · ${solicitud.tipoTexto}">
            <n:estado valor="${solicitud.estado.texto}" color="${solicitud.estado.color}"/>
        </n:encabezado>

        <%-- Siguiente paso según el estado --%>
        <c:set var="estado" value="${solicitud.estado}"/>
        <div class="cop-aviso d-flex flex-wrap justify-content-between align-items-center gap-2 my-3">
            <c:choose>
                <c:when test="${estado eq 'EN_ESPERA_VALIDACION'}">
                    <span>Debe esperar la aprobación antes de pagar.</span>
                    <a class="btn btn-primary btn-sm" href="<c:url value='/copias/validacion/${solicitud.id}'/>">Validar solicitud</a>
                </c:when>
                <c:when test="${estado eq 'APROBADO' or estado eq 'LISTO_PARA_PAGO'}">
                    <span>La solicitud está lista para el pago.</span>
                    <a class="btn btn-primary btn-sm" href="<c:url value='/copias/pagos/${solicitud.id}'/>">Ir al pago</a>
                </c:when>
                <c:when test="${estado eq 'PAGADO_EN_PREPARACION' or estado eq 'LISTO_PARA_RECOGER'}">
                    <span>
                        <c:out value="${estado eq 'LISTO_PARA_RECOGER' ? 'Ya se avisó al solicitante. Falta entregar la copia.' : 'Pagada. La copia está en preparación.'}"/>
                    </span>
                    <span class="d-flex flex-wrap gap-2">
                        <a class="btn btn-sij-secundario btn-sm" href="<c:url value='/copias/pagos/${solicitud.id}/comprobante'/>">Ver comprobante</a>
                        <n:confirmar accion="/copias/entregas/${solicitud.id}/avisar"
                                     texto="¿Enviar a ${solicitud.solicitanteCorreo} el aviso de que su copia está lista para recoger?"
                                     boton="Avisar que está lista" clase="btn btn-sij-secundario btn-sm">
                            <input type="hidden" name="volver" value="detalle">
                        </n:confirmar>
                        <n:confirmar accion="/copias/entregas/${solicitud.id}/entregar"
                                     texto="Verifique la identificación del solicitante en ventanilla. ¿Registrar la entrega de ${solicitud.folio}? El folio quedará inhabilitado."
                                     boton="Imprimir y entregar" clase="btn btn-primary btn-sm">
                            <input type="hidden" name="volver" value="detalle">
                        </n:confirmar>
                    </span>
                </c:when>
                <c:when test="${estado eq 'ENTREGADO'}">
                    <span>Copia entregada el <c:out value="${entrega.fechaTexto}"/>. El folio está inhabilitado.</span>
                    <a class="btn btn-sij-secundario btn-sm" href="<c:url value='/copias/pagos/${solicitud.id}/comprobante'/>">Ver comprobante</a>
                </c:when>
                <c:when test="${estado eq 'CANCELADO'}">
                    <span>Solicitud rechazada: <c:out value="${solicitud.motivoRechazo}"/></span>
                </c:when>
            </c:choose>
        </div>

        <div class="row g-3">
            <div class="col-lg-7">
                <n:tarjeta titulo="Datos de la solicitud">
                    <div class="cop-dato"><span>Folio</span><strong><c:out value="${solicitud.folio}"/></strong></div>
                    <div class="cop-dato"><span>Fecha de solicitud</span><strong><c:out value="${solicitud.fechaSolicitudTexto}"/></strong></div>
                    <div class="cop-dato"><span>Expediente</span><strong><c:out value="${solicitud.numeroExpediente}"/> (<c:out value="${solicitud.tipoTexto}"/>)</strong></div>
                    <div class="cop-dato"><span>Juzgado</span><strong><c:out value="${solicitud.juzgado}"/></strong></div>
                    <div class="cop-dato"><span>Materia</span><strong><c:out value="${solicitud.materia}"/></strong></div>
                    <div class="cop-dato"><span>Costo</span><strong><c:out value="${solicitud.costoTexto}"/></strong></div>
                    <c:if test="${not empty pago}">
                        <div class="cop-dato"><span>Comprobante</span><strong><c:out value="${pago.folioPago}"/> · <c:out value="${pago.fechaTexto}"/></strong></div>
                    </c:if>
                    <c:if test="${not empty solicitud.fechaValidacion}">
                        <div class="cop-dato"><span>Validada</span><strong><c:out value="${solicitud.fechaValidacionTexto}"/></strong></div>
                    </c:if>
                </n:tarjeta>

                <n:tarjeta titulo="Solicitante">
                    <div class="cop-dato"><span>Nombre</span><strong><c:out value="${solicitud.solicitanteNombre}"/></strong></div>
                    <div class="cop-dato"><span>RFC</span><strong><c:out value="${solicitud.solicitanteRfc}"/></strong></div>
                    <div class="cop-dato"><span>Correo</span><strong><c:out value="${solicitud.solicitanteCorreo}"/></strong></div>
                    <c:if test="${solicitud.privada}">
                        <div class="cop-dato">
                            <span>Identificación (INE)</span>
                            <a href="<c:url value='/copias/validacion/${solicitud.id}'/>">Ver en la validación</a>
                        </div>
                    </c:if>
                </n:tarjeta>
            </div>

            <div class="col-lg-5">
                <n:tarjeta titulo="Historial">
                    <ol class="cop-historial">
                        <c:forEach var="h" items="${historial}">
                            <li>
                                <div class="fw-semibold"><c:out value="${h.estadoNuevo.texto}"/></div>
                                <div class="sij-texto-chico"><c:out value="${h.fechaTexto}"/></div>
                                <c:if test="${not empty h.comentario}">
                                    <div class="sij-texto-chico"><c:out value="${h.comentario}"/></div>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ol>
                </n:tarjeta>

                <n:tarjeta titulo="Correos enviados">
                    <c:choose>
                        <c:when test="${empty correos}">
                            <p class="sij-descripcion mb-0">Aún no se ha enviado ningún correo.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="m" items="${correos}">
                                <div class="cop-dato">
                                    <span><c:out value="${m.tipo.texto}"/><br><small><c:out value="${m.enviadoEnTexto}"/></small></span>
                                    <n:estado valor="${m.exito ? 'Enviado' : 'Falló'}" color="${m.exito ? 'verde' : 'rojo'}"/>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </n:tarjeta>
            </div>
        </div>
    </jsp:body>
</t:layout>
