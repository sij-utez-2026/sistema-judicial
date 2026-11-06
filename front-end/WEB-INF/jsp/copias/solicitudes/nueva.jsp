<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%@ taglib prefix="cop" tagdir="/WEB-INF/tags/copias" %>
<%--
  RF 2.1 y 2.2: el trabajador captura la solicitud con los datos que le da el solicitante.
  Paso 1: buscar el expediente (Oficialía dice si es público o privado).
  Paso 2: datos del solicitante; la INE solo si es privado.
--%>
<t:layout titulo="Nueva solicitud" modulo="copias" seccion="nueva">
    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>
    <jsp:body>
        <n:migas><a href="<c:url value='/copias/solicitudes'/>">Registro de solicitudes</a> / Nueva solicitud</n:migas>
        <n:encabezado titulo="Nueva solicitud"
                      descripcion="Capture los datos que le proporciona el solicitante. Los campos con * son obligatorios."/>

        <%-- Paso 1: buscar expediente --%>
        <n:tarjeta titulo="1. Expediente" clase="mt-3">
            <form method="get" action="<c:url value='/copias/solicitudes/nueva'/>" class="row g-2 align-items-end">
                <div class="col-md-6">
                    <label for="expediente" class="form-label">Número de expediente *</label>
                    <input type="text" class="form-control" id="expediente" name="expediente" required maxlength="30"
                           value="<c:out value='${solicitudForm.numeroExpediente}'/>" placeholder="EXP-2026-001"
                           autocomplete="off">
                </div>
                <div class="col-md-auto">
                    <button type="submit" class="btn btn-sij-secundario">
                        <n:icono nombre="buscar" tamano="14"/> Buscar expediente
                    </button>
                </div>
                <div class="col-12 sij-texto-chico">
                    Expedientes de prueba: EXP-2026-001 y EXP-2026-003 (públicos), EXP-2026-002 y EXP-2026-004 (privados).
                </div>
            </form>

            <c:if test="${not empty expediente}">
                <div class="sij-tarjeta p-3 mt-3 cop-expediente">
                    <div class="d-flex flex-wrap justify-content-between gap-2">
                        <div>
                            <div class="fw-semibold"><c:out value="${expediente.numeroExpediente()}"/></div>
                            <div class="sij-texto-chico">
                                <c:out value="${expediente.juzgado()}"/> · Materia <c:out value="${expediente.materia()}"/>
                            </div>
                        </div>
                        <n:estado valor="${expediente.tipo() eq 'PRIVADO' ? 'Privado' : 'Público'}"
                                  color="${expediente.tipo() eq 'PRIVADO' ? 'ambar' : 'azul'}"/>
                    </div>
                    <p class="mb-0 mt-2 sij-texto-chico">
                        <c:choose>
                            <c:when test="${expediente.tipo() eq 'PRIVADO'}">
                                Expediente privado: adjunte la INE del solicitante. La solicitud quedará en espera de validación
                                y no se podrá pagar hasta tener el visto bueno.
                            </c:when>
                            <c:otherwise>
                                Expediente público: no se pide identificación. Al guardar pasa directo al pago.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:if>
        </n:tarjeta>

        <%-- Paso 2: datos del solicitante (solo cuando ya se encontró el expediente) --%>
        <c:if test="${not empty expediente}">
            <c:set var="privado" value="${expediente.tipo() eq 'PRIVADO'}"/>
            <form:form method="post" modelAttribute="solicitudForm" servletRelativeAction="/copias/solicitudes/nueva"
                       enctype="multipart/form-data" novalidate="novalidate">
                <form:hidden path="numeroExpediente"/>

                <n:tarjeta titulo="2. Datos del solicitante">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <form:label path="solicitanteNombre" cssClass="form-label">Nombre completo *</form:label>
                            <form:input path="solicitanteNombre" maxlength="150" autocomplete="name"
                                        cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                            <form:errors path="solicitanteNombre" cssClass="invalid-feedback" element="div"/>
                        </div>
                        <div class="col-md-6">
                            <form:label path="solicitanteRfc" cssClass="form-label">RFC *</form:label>
                            <form:input path="solicitanteRfc" maxlength="13" placeholder="12 o 13 caracteres"
                                        cssClass="form-control text-uppercase" cssErrorClass="form-control text-uppercase is-invalid"/>
                            <form:errors path="solicitanteRfc" cssClass="invalid-feedback" element="div"/>
                        </div>
                        <div class="col-md-6">
                            <form:label path="solicitanteCorreo" cssClass="form-label">Correo electrónico *</form:label>
                            <form:input path="solicitanteCorreo" type="email" maxlength="150" autocomplete="email"
                                        cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                            <form:errors path="solicitanteCorreo" cssClass="invalid-feedback" element="div"/>
                            <div class="form-text">Ahí se envían el comprobante de pago y el aviso de que la copia está lista.</div>
                        </div>
                        <div class="col-md-6">
                            <span class="form-label d-block">Juzgado y materia</span>
                            <div class="form-control-plaintext">
                                <c:out value="${expediente.juzgado()}"/> · <c:out value="${expediente.materia()}"/>
                            </div>
                        </div>
                    </div>

                    <c:if test="${privado}">
                        <div class="border-top pt-3 mt-3">
                            <n:archivo nombre="ine" etiqueta="Identificación oficial (INE)" tipos="${tiposIne}"
                                       maxMb="${maxMbIne}" requerido="true"/>
                            <div class="cop-aviso sij-texto-chico">
                                La INE solo la ve el personal de Copias al validar. No aparece en el comprobante.
                            </div>
                        </div>
                    </c:if>
                </n:tarjeta>

                <div class="d-flex justify-content-end gap-2">
                    <a class="btn btn-sij-secundario" href="<c:url value='/copias/solicitudes'/>">Cancelar</a>
                    <button type="submit" class="btn btn-primary">
                        <c:out value="${privado ? 'Guardar y enviar a validación' : 'Guardar y continuar al pago'}"/>
                    </button>
                </div>
            </form:form>
        </c:if>
    </jsp:body>
</t:layout>
