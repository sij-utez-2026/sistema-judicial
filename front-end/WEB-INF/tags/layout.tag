<%@ tag description="Plantilla común con menú lateral (INTEGRADORES)" pageEncoding="UTF-8" %>
<%--
  Uso en una pantalla de un módulo (ver front-end/README.md):

    <t:layout titulo="Catálogo de peritos" modulo="peritajes" seccion="peritos">
        ... contenido ...
    </t:layout>

  titulo  : texto de la pestaña del navegador (obligatorio).
  modulo  : prefijo del módulo (oficialia, audiencias, peritajes, segunda, copias). Muestra su menú,
            que está en front-end/WEB-INF/jsp/<modulo>/menu.jsp. Vacío = páginas del núcleo.
  seccion : opción del menú del módulo que se marca como activa (la "clave" de <n:menu-item>).
  estilos / scripts : CSS y JS propios de la pantalla. Si se usan, el contenido va dentro de <jsp:body>.
--%>
<%@ attribute name="titulo" required="true" %>
<%@ attribute name="modulo" required="false" %>
<%@ attribute name="seccion" required="false" %>
<%@ attribute name="estilos" fragment="true" required="false" %>
<%@ attribute name="scripts" fragment="true" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>

<%-- Módulo actual y opción activa: quedan en la petición para el menú del módulo y <n:migas> --%>
<c:remove var="moduloActual" scope="request"/>
<c:forEach var="m" items="${modulosSistema}">
    <c:if test="${m.prefijo eq modulo}">
        <c:set var="moduloActual" value="${m}" scope="request"/>
    </c:if>
</c:forEach>
<c:set var="seccionActual" value="${seccion}" scope="request"/>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%-- Token para fetch con POST (SIJ.encabezadosCsrf en comun.js). Aparece cuando exista Spring Security. --%>
    <c:if test="${not empty _csrf}">
        <meta name="_csrf" content="<c:out value='${_csrf.token}'/>">
        <meta name="_csrf_header" content="<c:out value='${_csrf.headerName}'/>">
    </c:if>
    <title><c:out value="${titulo}"/> · <c:out value="${empty moduloActual ? 'Sistema Judicial Integral' : moduloActual.sigla}"/></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<c:url value='/recursos/nucleo/css/estilos.css'/>">
    <jsp:invoke fragment="estilos"/>
</head>
<body class="sij-body">
<a class="visually-hidden-focusable position-absolute top-0 start-0 m-2 btn btn-light" href="#contenido">Saltar al contenido</a>

<div class="sij-app">
    <%-- ===== Menú lateral. En pantallas chicas se abre con el botón ☰ del encabezado. ===== --%>
    <aside class="sij-sidebar offcanvas-lg offcanvas-start" tabindex="-1" id="sijMenu" aria-label="Menú principal">
        <div class="offcanvas-body">
            <div class="d-flex justify-content-between align-items-start">
                <a class="sij-emblema flex-grow-1" href="<c:url value='/'/>">Reservado para<br>emblema oficial</a>
                <button type="button" class="btn-close btn-close-white ms-2 d-lg-none" data-bs-dismiss="offcanvas"
                        data-bs-target="#sijMenu" aria-label="Cerrar menú"></button>
            </div>
            <div class="sij-linea-dorada"></div>
            <div class="sij-institucion">Sistema Judicial Integral</div>
            <div class="sij-subtitulo-dorado mt-1">
                <c:out value="${empty moduloActual ? 'Inicio' : moduloActual.nombreLargo}"/>
            </div>

            <%-- Menú del módulo: front-end/WEB-INF/jsp/<modulo>/menu.jsp (lo mantiene cada equipo) --%>
            <c:if test="${not empty moduloActual}">
                <div class="sij-etiqueta-menu">Espacio de trabajo</div>
                <nav class="nav flex-column sij-menu" aria-label="Menú de <c:out value='${moduloActual.nombre}'/>">
                    <jsp:include page="/WEB-INF/jsp/${moduloActual.prefijo}/menu.jsp"/>
                </nav>
            </c:if>

            <div class="sij-etiqueta-menu">Sistema</div>
            <nav class="nav flex-column sij-menu" aria-label="Menú del sistema">
                <n:menu-item clave="inicio-sistema" url="/" texto="Inicio del sistema" icono="inicio"/>
                <%-- TODO núcleo: /notificaciones y /cuenta/password --%>
                <n:menu-item clave="notificaciones" texto="Notificaciones" icono="campana"/>
            </nav>

            <%-- TODO núcleo: nombre y rol del usuario en sesión (SesionService) --%>
            <div class="sij-usuario">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <div class="sij-avatar" aria-hidden="true"><c:out value="${usuarioActual.iniciales}"/></div>
                    <div>
                        <div class="fw-semibold"><c:out value="${usuarioActual.nombre}"/></div>
                        <div class="sij-subtitulo-dorado"><c:out value="${usuarioActual.rol}"/></div>
                    </div>
                </div>
                <%-- TODO núcleo: con Spring Security, formulario POST /logout con <n:csrf/> --%>
                <button type="button" class="btn sij-btn-salir w-100 text-start" disabled>&#8614; Cerrar sesión</button>
            </div>
        </div>
    </aside>

    <div class="sij-contenido">
        <%-- ===== Encabezado blanco ===== --%>
        <header class="sij-topbar d-flex align-items-center gap-2">
            <button class="btn btn-sm btn-outline-secondary d-lg-none" type="button" data-bs-toggle="offcanvas"
                    data-bs-target="#sijMenu" aria-controls="sijMenu" aria-label="Abrir menú">&#9776;</button>
            <span class="text-truncate">
                <c:out value="${empty moduloActual ? 'Sistema Judicial Integral' : moduloActual.nombreLargo}"/>
            </span>
            <span class="ms-auto d-none d-md-inline text-nowrap"><c:out value="${fechaHoraActual}"/></span>
        </header>

        <main class="sij-main" id="contenido">
            <n:mensajes/>
            <jsp:doBody/>
        </main>

        <footer class="sij-pie">
            Sistema Judicial Integral<c:if test="${not empty moduloActual}"> · <c:out value="${moduloActual.nombre}"/></c:if>
        </footer>
    </div>
</div>

<%-- Ventana de confirmación común: la usan <n:confirmar>, data-confirmar y SIJ.confirmar() --%>
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
