<%@ tag description="Íconos SVG del sistema (sin librerías externas)" pageEncoding="UTF-8" %>
<%--
    <n:icono nombre="personas"/>      <n:icono nombre="buscar" tamano="14"/>
  Nombres: inicio, personas, persona, carpeta, documento, calendario, balanza, grafica, campana,
           bitacora, edificio, buscar, editar, mas, salir, punto.
  ¿Falta uno? Pedirlo a los integradores para que todos los módulos usen los mismos.
--%>
<%@ attribute name="nombre" required="true" %>
<%@ attribute name="tamano" required="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="t" value="${empty tamano ? '16' : tamano}"/>
<svg width="<c:out value='${t}'/>" height="<c:out value='${t}'/>" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.4" aria-hidden="true" focusable="false">
<c:choose>
    <c:when test="${nombre eq 'inicio'}"><rect x="1.5" y="1.5" width="5" height="5"/><rect x="9.5" y="1.5" width="5" height="5"/><rect x="1.5" y="9.5" width="5" height="5"/><rect x="9.5" y="9.5" width="5" height="5"/></c:when>
    <c:when test="${nombre eq 'personas'}"><circle cx="6" cy="5" r="2.5"/><path d="M1.5 14c0-2.5 2-4.5 4.5-4.5s4.5 2 4.5 4.5"/><circle cx="11.5" cy="5.5" r="2"/><path d="M11 9.6c2 .2 3.5 2 3.5 4.4"/></c:when>
    <c:when test="${nombre eq 'persona'}"><circle cx="8" cy="5" r="3"/><path d="M2.5 14.5c0-3 2.5-5 5.5-5s5.5 2 5.5 5"/></c:when>
    <c:when test="${nombre eq 'carpeta'}"><path d="M1.5 4.5h5l1.5 1.5h6.5v7.5h-13z"/></c:when>
    <c:when test="${nombre eq 'documento'}"><path d="M3.5 1.5h6l3 3v10h-9z"/><path d="M9.5 1.5v3h3M5.5 8h5M5.5 10.5h5"/></c:when>
    <c:when test="${nombre eq 'calendario'}"><rect x="1.5" y="3" width="13" height="11.5" rx="1"/><path d="M1.5 6.5h13M5 1.5v3M11 1.5v3"/></c:when>
    <c:when test="${nombre eq 'balanza'}"><path d="M8 1.5v13M4 14.5h8M2.5 4h11"/><path d="M4.5 4l-2.5 5h5zM11.5 4l-2.5 5h5z"/></c:when>
    <c:when test="${nombre eq 'grafica'}"><path d="M1.5 14.5h13"/><rect x="2.5" y="8" width="2.5" height="6"/><rect x="6.75" y="4" width="2.5" height="10"/><rect x="11" y="10" width="2.5" height="4"/></c:when>
    <c:when test="${nombre eq 'campana'}"><path d="M4 11V7a4 4 0 0 1 8 0v4l1.5 1.5h-11z"/><path d="M6.5 14a1.5 1.5 0 0 0 3 0"/></c:when>
    <c:when test="${nombre eq 'bitacora'}"><rect x="2.5" y="1.5" width="11" height="13" rx="1"/><path d="M5 5h6M5 8h6M5 11h4"/></c:when>
    <c:when test="${nombre eq 'edificio'}"><path d="M1.5 14.5h13M2.5 6.5h11L8 2.5zM4 7v6M8 7v6M12 7v6"/></c:when>
    <c:when test="${nombre eq 'buscar'}"><circle cx="7" cy="7" r="5"/><path d="M11 11l3.5 3.5"/></c:when>
    <c:when test="${nombre eq 'editar'}"><path d="M11 2.5l2.5 2.5L5 13.5H2.5V11z"/></c:when>
    <c:when test="${nombre eq 'mas'}"><path d="M8 2.5v11M2.5 8h11"/></c:when>
    <c:when test="${nombre eq 'salir'}"><path d="M6 2.5H2.5v11H6M10 5l3 3-3 3M13 8H6"/></c:when>
    <c:otherwise><circle cx="8" cy="8" r="2"/></c:otherwise>
</c:choose>
</svg>
