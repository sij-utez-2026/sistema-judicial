<%@ tag description="Plantilla pública sin menú (INTEGRADORES)" pageEncoding="UTF-8" %>
<%--
  Para páginas sin sesión: portal público de Copias (/copias/publico/**) y páginas de error.
  Igual que layout.tag pero sin menú lateral ni usuario.

    <t:layout-publico titulo="Consultar folio" subtitulo="Copias Certificadas">
        ... contenido ...
    </t:layout-publico>
--%>
<%@ attribute name="titulo" required="true" %>
<%@ attribute name="subtitulo" required="false" %>
<%@ attribute name="estilos" fragment="true" required="false" %>
<%@ attribute name="scripts" fragment="true" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <c:if test="${not empty _csrf}">
        <meta name="_csrf" content="<c:out value='${_csrf.token}'/>">
        <meta name="_csrf_header" content="<c:out value='${_csrf.headerName}'/>">
    </c:if>
    <title><c:out value="${titulo}"/> · Sistema Judicial Integral</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<c:url value='/recursos/nucleo/css/estilos.css'/>">
    <jsp:invoke fragment="estilos"/>
</head>
<body class="sij-body">
<div class="d-flex flex-column min-vh-100">
    <header class="sij-topbar-publico d-flex align-items-center gap-3">
        <span class="fw-semibold">Sistema Judicial Integral</span>
        <c:if test="${not empty subtitulo}">
            <span class="sij-subtitulo-dorado fs-6"><c:out value="${subtitulo}"/></span>
        </c:if>
    </header>

    <main class="sij-main-publico" id="contenido">
        <n:mensajes/>
        <jsp:doBody/>
    </main>

    <footer class="sij-pie text-center">Sistema Judicial Integral</footer>
</div>

<div class="modal fade" id="sijConfirmar" tabindex="-1" aria-labelledby="sijConfirmarTitulo" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="modal-title fs-5" id="sijConfirmarTitulo">Confirmar acción</h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body" data-mensaje></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sij-secundario" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" data-aceptar>Aceptar</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/recursos/nucleo/js/comun.js'/>"></script>
<jsp:invoke fragment="scripts"/>
</body>
</html>
