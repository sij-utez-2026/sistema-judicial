<%@ tag description="CSS de Copias y colores del área (EQUIPO 5)" pageEncoding="UTF-8" %>
<%--
  Va en el atributo "estilos" de <t:layout> en TODAS las pantallas de Copias:

    <jsp:attribute name="estilos"><cop:estilos/></jsp:attribute>

  1. Carga recursos/copias/css/copias.css.
  2. Escribe los colores que configuró COPIAS_ADMIN (coloresCopias, lo pone CopiasVistasAdvice).
     Solo cambian las pantallas de Copias: el estilos.css del núcleo no se toca.
     Los valores ya vienen validados como #RRGGBB desde ColoresCopiasService.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<link rel="stylesheet" href="<c:url value='/recursos/copias/css/copias.css'/>">
<c:if test="${not empty coloresCopias}">
    <style>
        :root {
        <c:forEach var="color" items="${coloresCopias}">
            <c:out value="${color.key}"/>: <c:out value="${color.value}"/>;
        </c:forEach>
        }
    </style>
</c:if>
