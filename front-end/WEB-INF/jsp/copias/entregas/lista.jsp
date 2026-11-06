<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- RF 5.1 Entregas: pagadas que falta entregar. Avisar por correo y registrar la entrega --%>
<t:layout titulo="Entregas" modulo="copias" seccion="entregas">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>Entregas</n:migas>
        <n:encabezado titulo="Entregas pendientes"
                      descripcion="Copias pagadas. Cuando una copia esté lista, avise al solicitante; al imprimirla y entregarla, regístrelo."/>

        <div class="cop-aviso my-3 sij-texto-chico">
            Antes de entregar, verifique la identificación del solicitante en ventanilla. Después de entregar, el folio queda inhabilitado.
        </div>

        <n:tarjeta sinRelleno="true">
            <c:choose>
                <c:when test="${empty pendientes}">
                    <div class="p-4 text-center sij-descripcion">No hay copias pendientes de entrega.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table sij-tabla align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Folio / Comprobante</th>
                                <th>Expediente</th>
                                <th>Solicitante</th>
                                <th>Estado</th>
                                <th class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="s" items="${pendientes}">
                                <c:set var="pago" value="${pagos[s.id]}"/>
                                <tr>
                                    <td>
                                        <a class="fw-semibold" href="<c:url value='/copias/solicitudes/${s.id}'/>"><c:out value="${s.folio}"/></a>
                                        <div class="sij-texto-chico"><c:out value="${pago.folioPago}"/> · <c:out value="${pago.fechaTexto}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.numeroExpediente}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.juzgado}"/></div>
                                    </td>
                                    <td>
                                        <c:out value="${s.solicitanteNombre}"/>
                                        <div class="sij-texto-chico"><c:out value="${s.solicitanteCorreo}"/></div>
                                    </td>
                                    <td><n:estado valor="${s.estado.texto}" color="${s.estado.color}"/></td>
                                    <td class="text-end">
                                        <div class="d-flex flex-wrap gap-2 justify-content-end">
                                            <n:confirmar accion="/copias/entregas/${s.id}/avisar"
                                                         texto="¿Enviar a ${s.solicitanteCorreo} el aviso de que su copia está lista para recoger?"
                                                         boton="${s.estado eq 'LISTO_PARA_RECOGER' ? 'Reenviar aviso' : 'Avisar que está lista'}"
                                                         clase="btn btn-sm btn-sij-secundario"/>
                                            <n:confirmar accion="/copias/entregas/${s.id}/entregar"
                                                         texto="Verifique la identificación del solicitante en ventanilla. ¿Registrar la entrega de ${s.folio}? El folio quedará inhabilitado."
                                                         boton="Entregar" clase="btn btn-sm btn-primary"/>
                                        </div>
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
