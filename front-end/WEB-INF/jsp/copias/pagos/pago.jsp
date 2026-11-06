<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%-- RF 4.1 Pago simulado: resumen con el costo congelado y confirmación --%>
<t:layout titulo="Pago ${solicitud.folio}" modulo="copias" seccion="solicitudes">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas>
            <a href="<c:url value='/copias/solicitudes'/>">Registro de solicitudes</a> /
            <a href="<c:url value='/copias/solicitudes/${solicitud.id}'/>"><c:out value="${solicitud.folio}"/></a> / Pago
        </n:migas>
        <n:encabezado titulo="Registrar pago" descripcion="Revise el resumen con el solicitante y confirme el pago.">
            <n:estado valor="${solicitud.estado.texto}" color="${solicitud.estado.color}"/>
        </n:encabezado>

        <div class="row g-3 mt-1">
            <div class="col-lg-7">
                <n:tarjeta titulo="Resumen">
                    <div class="cop-dato"><span>Folio de la solicitud</span><strong><c:out value="${solicitud.folio}"/></strong></div>
                    <div class="cop-dato"><span>Expediente</span><strong><c:out value="${solicitud.numeroExpediente}"/> (<c:out value="${solicitud.tipoTexto}"/>)</strong></div>
                    <div class="cop-dato"><span>Juzgado</span><strong><c:out value="${solicitud.juzgado}"/></strong></div>
                    <div class="cop-dato"><span>Solicitante</span><strong><c:out value="${solicitud.solicitanteNombre}"/></strong></div>
                    <div class="cop-dato"><span>Correo para el comprobante</span><strong><c:out value="${solicitud.solicitanteCorreo}"/></strong></div>
                    <div class="cop-dato fs-5"><span>Importe</span><strong><c:out value="${solicitud.costoTexto}"/></strong></div>
                </n:tarjeta>
            </div>
            <div class="col-lg-5">
                <n:tarjeta titulo="Pago">
                    <div class="sij-aviso sij-aviso-advertencia mb-3">Pago simulado — no se realiza ningún cargo.</div>
                    <c:choose>
                        <c:when test="${sePuedePagar}">
                            <p class="sij-descripcion">Al confirmar, el comprobante se envía a
                                <strong><c:out value="${solicitud.solicitanteCorreo}"/></strong> y también se podrá imprimir.</p>
                            <n:confirmar accion="/copias/pagos/${solicitud.id}"
                                         texto="¿Confirmar el pago de ${solicitud.costoTexto} de la solicitud ${solicitud.folio}?"
                                         boton="Confirmar pago" clase="btn btn-primary w-100"/>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning mb-0" role="status"><c:out value="${motivoNoPago}"/></div>
                        </c:otherwise>
                    </c:choose>
                </n:tarjeta>
            </div>
        </div>
    </jsp:body>
</t:layout>
