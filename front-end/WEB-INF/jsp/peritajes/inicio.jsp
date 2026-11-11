<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Inicio del módulo (/peritajes). EQUIPO 3.
  TODO: inicio según el rol (docs/modulos/peritajes.md, sección 8): casos para el administrador, mis peritajes para el perito.
--%>
<t:layout titulo="Inicio" modulo="peritajes" seccion="inicio">
    <n:migas>Inicio</n:migas>
    <n:encabezado titulo="${moduloActual.nombreLargo}" descripcion="${moduloActual.descripcion}"/>

    <div class="row g-3 mt-1">
        <div class="col-md-6 col-xl-4">
            <a class="sij-tarjeta p-4 h-100" href="<c:url value='/peritajes/peritos'/>">
                <h2 class="sij-tarjeta-titulo mb-1">Catálogo de peritos</h2>
                <p class="sij-descripcion mb-0">Padrón institucional, especialidades y disponibilidad para asignación.</p>
            </a>
        </div>
    </div>
</t:layout>
