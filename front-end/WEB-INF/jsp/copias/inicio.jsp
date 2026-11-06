<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Inicio del módulo (/copias). EQUIPO 5: es la página de ejemplo para copiar.
  Toda pantalla del módulo empieza igual: <t:layout modulo="copias" seccion="...">, <n:migas> y <n:encabezado>.
  Guía completa: front-end/README.md
--%>
<t:layout titulo="Inicio" modulo="copias" seccion="inicio">
    <n:migas>Inicio</n:migas>
    <n:encabezado titulo="${moduloActual.nombre}" descripcion="${moduloActual.descripcion}"/>

    <n:tarjeta titulo="Pantalla de inicio de ejemplo" clase="mt-3">
        <p class="sij-descripcion">
            Equipo 5: reemplacen este contenido por el inicio según el rol que pide su DFR
            (<code>docs/modulos/copias-certificadas.md</code>, sección 8). Las opciones del menú lateral están en
            <code>front-end/WEB-INF/jsp/copias/menu.jsp</code>.
        </p>
        <n:estado valor="Pendiente"/>
    </n:tarjeta>
</t:layout>
