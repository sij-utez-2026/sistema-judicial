<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%-- Inicio del sistema (/): una tarjeta por módulo. INTEGRADORES. --%>
<t:layout titulo="Inicio" seccion="inicio-sistema">
    <n:migas>Módulos del sistema</n:migas>
    <n:encabezado titulo="Sistema Judicial Integral"
                  descripcion="Elija el módulo en el que va a trabajar."/>

    <%-- TODO núcleo: con Spring Security solo aparecen los módulos de los roles del usuario --%>
    <div class="row g-3 mt-1">
        <c:forEach var="m" items="${modulosSistema}">
            <div class="col-md-6 col-xl-4">
                <a class="sij-tarjeta p-4 h-100" href="<c:url value='/${m.prefijo}'/>">
                    <div class="sij-texto-chico mb-1">
                        <c:out value="${m.sigla}"/> · Equipo <c:out value="${m.equipo}"/>
                    </div>
                    <h2 class="sij-tarjeta-titulo mb-1"><c:out value="${m.nombre}"/></h2>
                    <p class="sij-descripcion mb-0"><c:out value="${m.descripcion}"/></p>
                </a>
            </div>
        </c:forEach>
    </div>
</t:layout>
