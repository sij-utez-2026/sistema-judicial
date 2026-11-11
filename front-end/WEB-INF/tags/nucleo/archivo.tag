<%@ tag description="Campo para subir un archivo" pageEncoding="UTF-8" %>
<%--
    <n:archivo nombre="acta" etiqueta="Acta firmada" tipos=".pdf,.jpg,.png" maxMb="5" requerido="true"/>
  El formulario necesita enctype="multipart/form-data". El tamaño se revisa en el navegador (comun.js),
  pero la validación real la hace ArchivoService del núcleo en el servidor.
--%>
<%@ attribute name="nombre" required="true" %>
<%@ attribute name="etiqueta" required="false" %>
<%@ attribute name="tipos" required="false" %>
<%@ attribute name="maxMb" required="false" %>
<%@ attribute name="requerido" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<div class="mb-3">
    <c:if test="${not empty etiqueta}">
        <label for="<c:out value='${nombre}'/>" class="form-label"><c:out value="${etiqueta}"/><c:if test="${requerido}"> *</c:if></label>
    </c:if>
    <input type="file" class="form-control" id="<c:out value='${nombre}'/>" name="<c:out value='${nombre}'/>"
           <c:if test="${not empty tipos}">accept="<c:out value='${tipos}'/>"</c:if>
           <c:if test="${not empty maxMb}">data-max-mb="<c:out value='${maxMb}'/>"</c:if>
           <c:if test="${requerido}">required</c:if>>
    <div class="invalid-feedback" data-aviso-archivo></div>
    <c:if test="${not empty tipos or not empty maxMb}">
        <div class="form-text">
            <c:if test="${not empty tipos}">Formatos: <c:out value="${fn:toUpperCase(fn:replace(fn:replace(tipos, '.', ''), ',', ', '))}"/>.</c:if>
            <c:if test="${not empty maxMb}">Tamaño máximo: <c:out value="${maxMb}"/> MB.</c:if>
        </div>
    </c:if>
</div>
