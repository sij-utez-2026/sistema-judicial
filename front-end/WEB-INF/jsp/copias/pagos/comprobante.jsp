<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%--
  Comprobante de pago: se imprime (al imprimir solo sale el recuadro, ver copias.css @media print)
  y se envía por correo. Solo los datos esenciales: NUNCA la INE.
  Debajo, lo que sigue: entregar ahora si la copia está disponible, o dejarla pendiente y avisar después.
--%>
<t:layout titulo="Comprobante ${pago.folioPago}" modulo="copias" seccion="solicitudes">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/recursos/copias/js/copias.js'/>"></script>
    </jsp:attribute>
    <jsp:body>
        <n:migas>
            <a href="<c:url value='/copias/solicitudes'/>">Registro de solicitudes</a> /
            <a href="<c:url value='/copias/solicitudes/${solicitud.id}'/>"><c:out value="${solicitud.folio}"/></a> / Comprobante
        </n:migas>

        <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3 cop-no-imprimir">
            <h1 class="sij-titulo">Comprobante de pago</h1>
            <div class="d-flex flex-wrap gap-2">
                <button type="button" class="btn btn-sij-secundario" data-imprimir>Imprimir comprobante</button>
                <n:confirmar accion="/copias/pagos/${solicitud.id}/comprobante/enviar"
                             texto="¿Enviar de nuevo el comprobante a ${solicitud.solicitanteCorreo}?"
                             boton="Reenviar por correo"/>
            </div>
        </div>

        <%-- Lo que se imprime --%>
        <section class="sij-tarjeta p-4 mb-3 cop-comprobante">
            <div class="cop-comprobante-encabezado pb-3 mb-3 d-flex justify-content-between flex-wrap gap-2">
                <div>
                    <div class="sij-subtitulo-dorado">Sistema Judicial Integral · Copias Certificadas</div>
                    <h2 class="sij-tarjeta-titulo mb-0 mt-1">Comprobante de pago</h2>
                </div>
                <div class="text-end">
                    <div class="sij-texto-chico">Folio del comprobante</div>
                    <div class="fs-5 fw-semibold"><c:out value="${pago.folioPago}"/></div>
                </div>
            </div>

            <div class="cop-dato"><span>Folio de la solicitud</span><strong><c:out value="${solicitud.folio}"/></strong></div>
            <div class="cop-dato"><span>Expediente</span><strong><c:out value="${solicitud.numeroExpediente}"/></strong></div>
            <div class="cop-dato"><span>Juzgado</span><strong><c:out value="${solicitud.juzgado}"/></strong></div>
            <div class="cop-dato"><span>Materia</span><strong><c:out value="${solicitud.materia}"/></strong></div>
            <div class="cop-dato"><span>Solicitante</span><strong><c:out value="${solicitud.solicitanteNombre}"/></strong></div>
            <div class="cop-dato"><span>RFC</span><strong><c:out value="${solicitud.solicitanteRfc}"/></strong></div>
            <div class="cop-dato"><span>Correo</span><strong><c:out value="${solicitud.solicitanteCorreo}"/></strong></div>
            <div class="cop-dato"><span>Fecha de pago</span><strong><c:out value="${pago.fechaTexto}"/></strong></div>
            <div class="cop-dato fs-5"><span>Importe</span><strong><c:out value="${pago.montoTexto}"/></strong></div>
            <div class="cop-dato"><span>Estado</span><strong><c:out value="${solicitud.estado.texto}"/></strong></div>

            <p class="sij-texto-chico mt-3 mb-0">
                Pago simulado: no se realizó ningún cargo. Presente este comprobante y una identificación oficial
                al recoger su copia certificada en el juzgado.
            </p>
        </section>

        <%-- Qué sigue (no se imprime) --%>
        <div class="cop-no-imprimir">
            <c:choose>
                <c:when test="${solicitud.estado.pendienteDeEntrega()}">
                    <n:tarjeta titulo="Entrega de la copia">
                        <p class="sij-descripcion">
                            Si la copia está disponible, imprímala y entréguela ahora. Si no, déjela pendiente:
                            cuando esté lista, avise al solicitante por correo desde Entregas.
                        </p>
                        <div class="d-flex flex-wrap gap-2 justify-content-end">
                            <a class="btn btn-sij-secundario" href="<c:url value='/copias/entregas'/>">No está disponible: dejar pendiente</a>
                            <c:if test="${solicitud.estado ne 'LISTO_PARA_RECOGER'}">
                                <n:confirmar accion="/copias/entregas/${solicitud.id}/avisar"
                                             texto="¿Enviar a ${solicitud.solicitanteCorreo} el aviso de que su copia está lista para recoger?"
                                             boton="Avisar que está lista">
                                    <input type="hidden" name="volver" value="comprobante">
                                </n:confirmar>
                            </c:if>
                            <n:confirmar accion="/copias/entregas/${solicitud.id}/entregar"
                                         texto="Verifique la identificación del solicitante en ventanilla. ¿Registrar la entrega de ${solicitud.folio}? El folio quedará inhabilitado."
                                         boton="Imprimir copia y entregar ahora" clase="btn btn-primary">
                                <input type="hidden" name="volver" value="comprobante">
                            </n:confirmar>
                        </div>
                    </n:tarjeta>
                </c:when>
                <c:when test="${not empty entrega}">
                    <div class="cop-aviso mb-3">Copia entregada el <c:out value="${entrega.fechaTexto}"/>. El folio está inhabilitado.</div>
                </c:when>
            </c:choose>

            <n:tarjeta titulo="Correos enviados">
                <c:choose>
                    <c:when test="${empty correos}">
                        <p class="sij-descripcion mb-0">Aún no se ha enviado ningún correo.</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="m" items="${correos}">
                            <div class="cop-dato">
                                <span><c:out value="${m.tipo.texto}"/> a <c:out value="${m.destinatario}"/><br><small><c:out value="${m.enviadoEnTexto}"/></small></span>
                                <n:estado valor="${m.exito ? 'Enviado' : 'Falló'}" color="${m.exito ? 'verde' : 'rojo'}"/>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </n:tarjeta>
        </div>
    </jsp:body>
</t:layout>
