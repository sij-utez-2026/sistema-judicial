<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%-- RF 3.1 Registro de perito (boceto docs/modulos/peritajes-figma-registro.png) --%>
<t:layout titulo="Registrar perito" modulo="peritajes" seccion="peritos">
    <jsp:attribute name="estilos">
        <link rel="stylesheet" href="<c:url value='/recursos/peritajes/css/peritajes.css'/>">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/recursos/peritajes/js/peritos.js'/>"></script>
    </jsp:attribute>
    <jsp:body>
        <n:migas><a href="<c:url value='/peritajes/peritos'/>">Catálogo de peritos</a> / Registrar perito</n:migas>
        <n:encabezado titulo="Registrar perito"
                      descripcion="Capture los datos del padrón. Los campos con * son obligatorios."/>

        <%-- <form:form> muestra los errores de @Valid y, con Spring Security, agrega solo el token CSRF --%>
        <form:form method="post" modelAttribute="peritoForm" servletRelativeAction="/peritajes/peritos/nuevo"
                   novalidate="novalidate" cssClass="mt-3">

            <n:tarjeta titulo="Datos de identificación">
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

                    <div class="col-md-6">
                        <form:label path="correo" cssClass="form-label">Correo institucional *</form:label>
                        <form:input path="correo" type="email" maxlength="150" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                        <form:errors path="correo" cssClass="invalid-feedback" element="div"/>
                        <div class="form-text">No se podrá cambiar después del registro.</div>
                    </div>
                    <div class="col-md-6">
                        <form:label path="telefono" cssClass="form-label">Teléfono de contacto *</form:label>
                        <form:input path="telefono" type="tel" maxlength="14" placeholder="777 000 0000" data-telefono="data-telefono"
                                    cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                        <form:errors path="telefono" cssClass="invalid-feedback" element="div"/>
                    </div>

                    <div class="col-12">
                        <form:label path="numeroRegistro" cssClass="form-label">Número de registro *</form:label>
                        <form:input path="numeroRegistro" maxlength="8" placeholder="PER-0000" data-mayusculas="data-mayusculas"
                                    cssClass="form-control" cssErrorClass="form-control is-invalid"/>
                        <form:errors path="numeroRegistro" cssClass="invalid-feedback" element="div"/>
                        <div class="form-text">Identificador único dentro del padrón de peritos. No se podrá cambiar después del registro.</div>
                    </div>
                </div>
            </n:tarjeta>

            <n:tarjeta titulo="Perfil pericial">
                <%-- Especialidades en texto libre: una o varias (RF 3.1 y 3.3). peritos.js agrega y quita renglones. --%>
                <spring:bind path="especialidadCapturada"><c:set var="errorEspecialidad" value="${status.error}"/></spring:bind>
                <div class="mb-3">
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

                <fieldset class="mb-3">
                    <legend class="form-label fs-6">Materias de intervención *</legend>
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
                    <div class="form-text" data-contador-materias>Seleccione al menos una materia para la asignación.</div>
                </fieldset>

                <div class="d-flex justify-content-between align-items-center border-top pt-3">
                    <span class="form-label mb-0">Estado inicial</span>
                    <n:estado valor="Activo"/>
                </div>
            </n:tarjeta>

            <div class="d-flex justify-content-end gap-2">
                <a class="btn btn-sij-secundario" href="<c:url value='/peritajes/peritos'/>">Cancelar</a>
                <button type="submit" class="btn btn-primary">Guardar perito</button>
            </div>
        </form:form>
    </jsp:body>
</t:layout>
