<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Menú lateral del módulo de Copias Certificadas (EQUIPO 5). Lo incluye layout.tag cuando la pantalla usa modulo="copias".
  - clave: la misma que la pantalla pone en <t:layout seccion="...">, para marcarla activa.
  - Sin url = pantalla pendiente (aparece desactivada). Al terminar una pantalla, agregar su url.
  - TODO: con Spring Security, envolver cada opción en <sec:authorize access="hasRole('...')"> según la tabla de roles.
--%>
<n:menu-item clave="inicio" url="/copias" texto="Inicio" icono="inicio"/>
<n:menu-item clave="validacion" texto="Validación de solicitudes" icono="documento"/>
<n:menu-item clave="entregas" texto="Entregas" icono="carpeta"/>
<n:menu-item clave="solicitudes" texto="Consulta de solicitudes" icono="buscar"/>
