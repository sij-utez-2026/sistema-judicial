<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%-- RF 3.3 Edición y RF 3.4 Baja/reactivación de perito (boceto docs/modulos/peritajes-figma-editar.png) --%>
<t:layout titulo="Editar perito" modulo="peritajes" seccion="peritos">
    <jsp:attribute name="estilos">
        <link rel="stylesheet" href="<c:url value='/recursos/peritajes/css/peritajes.css'/>">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/recursos/peritajes/js/peritos.js'/>"></script>
    </jsp:attribute>
    <jsp:body>
        <n:migas><a href="<c:url value='/peritajes/peritos'/>">Catálogo de peritos</a> / Editar perito</n:migas>
        <n:encabezado titulo="Editar perito"
                      descripcion="${perito.nombreCompleto} · Registro ${perito.numeroRegistro}">
            <n:estado valor="${perito.activo ? 'Activo' : 'Inactivo'}"/>
        </n:encabezado>

        <div class="row g-3 mt-1">
            <div class="col-lg-8">

                <%-- Datos del perito. El botón de guardar está al final de la página (atributo form="formDatos") --%>
                <form:form id="formDatos" method="post" modelAttribute="peritoForm" novalidate="novalidate"
                           servletRelativeAction="/peritajes/peritos/${perito.id}/editar">
                    <n:tarjeta titulo="Datos del perito">
                        <div class="row g-3">
                            <div class="col-md-4">
                                <form:label path="nombre" cssClass="form-label">Nombre(s) *</form:label>
                                <form:input path="nombre" maxlength="80" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                                <form:errors path="nombre" cssClass="invalid-feedback" element="div"/>
                            </div>
                            <div class="col-md-4">
                                <form:label path="apellidoPaterno" cssClass="form-label">Apellido paterno *</form:label>
                                <form:input path="apellidoPaterno" maxlength="60" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                                <form:errors path="apellidoPaterno" cssClass="invalid-feedback" element="div"/>
                            </div>
                            <div class="col-md-4">
                                <form:label path="apellidoMaterno" cssClass="form-label">Apellido materno</form:label>
                                <form:input path="apellidoMaterno" maxlength="60" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                                <form:errors path="apellidoMaterno" cssClass="invalid-feedback" element="div"/>
                            </div>

                            <%-- Correo y número de registro: fijos (el service no los cambia) --%>
                            <div class="col-md-7">
                                <form:label path="correo" cssClass="form-label">Correo institucional</form:label>
                                <form:input path="correo" type="email" readonly="true" cssClass="form-control"/>
                            </div>
                            <div class="col-md-5">
                                <form:label path="numeroRegistro" cssClass="form-label">Número de registro</form:label>
                                <form:input path="numeroRegistro" readonly="true" cssClass="form-control"/>
                            </div>

                            <div class="col-md-7">
                                <form:label path="telefono" cssClass="form-label">Teléfono de contacto *</form:label>
                                <form:input path="telefono" type="tel" maxlength="14" data-telefono="data-telefono"
                                            cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                                <form:errors path="telefono" cssClass="invalid-feedback" element="div"/>
                            </div>

                            <%-- Especialidades en texto libre: una o varias (RF 3.1 y 3.3) --%>
                            <spring:bind path="especialidadCapturada"><c:set var="errorEspecialidad" value="${status.error}"/></spring:bind>
                            <div class="col-12">
                                <label class="form-label" for="especialidad0">Especialidades *</label>
                                <div data-especialidades>
                                    <c:forEach var="esp" items="${peritoForm.especialidades}" varStatus="st">
                                        <div class="input-group mb-2 per-especialidad">
                                            <input type="text" class="form-control${errorEspecialidad ? ' is-invalid' : ''}" maxlength="100"
                                                   id="especialidad${st.index}" name="especialidades[${st.index}]" value="<c:out value='${esp}'/>"
                                                   placeholder="Área o estudios del perito, por ejemplo: Psicología forense">
                                            <button type="button" class="btn btn-sij-secundario" data-quitar-especialidad
                                                    title="Quitar especialidad" aria-label="Quitar especialidad">&times;</button>
                                        </div>
                                    </c:forEach>
                                </div>
                                <form:errors path="especialidadCapturada" cssClass="text-danger small mb-1" element="div"/>
                                <button type="button" class="btn btn-sm btn-sij-secundario" data-agregar-especialidad>+ Agregar otra especialidad</button>
                            </div>

                            <fieldset class="col-12">
                                <legend class="form-label fs-6">Materias *</legend>
                                <div class="d-flex flex-wrap gap-2">
                                    <c:forEach var="m" items="${materias}">
                                        <span class="per-opcion">
                                            <form:checkbox path="materiasIds" value="${m.id}" id="materia${m.id}" autocomplete="off"
                                                           cssClass="btn-check per-materia"/>
                                            <label class="btn btn-sm" for="materia${m.id}"><c:out value="${m.nombre}"/></label>
                                        </span>
                                    </c:forEach>
                                </div>
                                <form:errors path="materiasIds" cssClass="text-danger small mt-1" element="div"/>
                                <div class="form-text"><span data-contador-materias></span>
                                    Cambiar las materias no afecta los peritajes ya asignados.</div>
                            </fieldset>
                        </div>
                    </n:tarjeta>
                </form:form>

                <%-- RF 3.4 Estado en el padrón (formulario aparte). peritos.js arma el texto de confirmación. --%>
                <form method="post" action="<c:url value='/peritajes/peritos/${perito.id}/estado'/>" data-formulario-estado
                      data-estado-actual="<c:out value='${perito.activo}'/>" data-nombre="<c:out value='${perito.nombreCompleto}'/>">
                    <n:csrf/>
                    <n:tarjeta titulo="Estado en el padrón">
                        <div class="btn-group d-flex mb-3" style="max-width: 220px;" role="group" aria-label="Estado del perito">
                            <input type="radio" class="btn-check per-segmento" name="activo" id="estadoActivo"
                                   value="true"${perito.activo ? ' checked' : ''}>
                            <label class="btn btn-sm px-3" for="estadoActivo">Activo</label>
                            <input type="radio" class="btn-check per-segmento" name="activo" id="estadoInactivo"
                                   value="false"${perito.activo ? '' : ' checked'}>
                            <label class="btn btn-sm px-3" for="estadoInactivo">Inactivo</label>
                        </div>

                        <%-- Aviso si tiene carga activa: no se puede desactivar --%>
                        <c:if test="${perito.activo and carga.total() gt 0}">
                            <div class="sij-aviso sij-aviso-advertencia p-3 mb-2">
                                <div class="fw-semibold">&#9432; No es posible desactivar al perito</div>
                                Tiene <c:out value="${carga.total()}"/> peritajes con carga activa:
                                <c:out value="${carga.asignados()}"/> Asignado y
                                <c:out value="${carga.enProceso()}"/> En Proceso.
                                Deben concluirse o ser reasignados por un usuario autorizado antes de cambiar a Inactivo.
                            </div>
                        </c:if>

                        <%-- Resultado de un intento fallido de baja --%>
                        <c:if test="${not empty errorEstado}">
                            <div class="sij-texto-chico mb-2">
                                <span class="text-danger"><c:out value="${errorEstado}"/></span><br>
                                El cambio a Inactivo no se aplicó.
                                <c:out value="${perito.nombreCompleto}"/> conserva el estado Activo.
                            </div>
                        </c:if>

                        <%-- peritos.js solo lo activa si se eligió otro estado --%>
                        <button type="submit" class="btn btn-sij-secundario" data-aplicar-estado>Aplicar cambio de estado</button>
                    </n:tarjeta>
                </form>

                <div class="d-flex justify-content-end gap-2">
                    <a class="btn btn-sij-secundario" href="<c:url value='/peritajes/peritos'/>">Cancelar</a>
                    <button type="submit" form="formDatos" class="btn btn-primary">Guardar datos del perito</button>
                </div>
            </div>

            <%-- Carga activa: peritajes Asignados y En Proceso --%>
            <div class="col-lg-4">
                <n:tarjeta titulo="Carga activa: ${carga.total()}">
                    <c:choose>
                        <c:when test="${empty peritajesActivos}">
                            <p class="sij-descripcion">Sin peritajes Asignados ni En Proceso.</p>
                        </c:when>
                        <c:otherwise>
                            <table class="table sij-tabla table-bordered mb-2">
                                <thead><tr><th>Folio</th><th>Estado</th></tr></thead>
                                <tbody>
                                <c:forEach var="pa" items="${peritajesActivos}">
                                    <tr>
                                        <td class="fw-semibold"><c:out value="${pa.folio()}"/></td>
                                        <td><n:estado valor="${pa.estado()}"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                    <div class="sij-texto-chico">Los dictámenes Entregados no forman parte de la carga activa.</div>
                </n:tarjeta>
            </div>
        </div>
    </jsp:body>
</t:layout>
