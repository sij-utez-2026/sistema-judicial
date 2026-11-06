<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Menú lateral del módulo de Segunda Instancia (EQUIPO 4). Lo incluye layout.tag cuando la pantalla usa modulo="segunda".
  - clave: la misma que la pantalla pone en <t:layout seccion="...">, para marcarla activa.
  - Sin url = pantalla pendiente (aparece desactivada). Al terminar una pantalla, agregar su url.
  - TODO: con Spring Security, envolver cada opción en <sec:authorize access="hasRole('...')"> según la tabla de roles.
--%>
<n:menu-item clave="inicio" url="/segunda" texto="Inicio" icono="inicio"/>
<n:menu-item clave="tocas" texto="Mis tocas" icono="carpeta"/>
<n:menu-item clave="resoluciones" texto="Resoluciones" icono="balanza"/>
<n:menu-item clave="salas" texto="Salas" icono="edificio"/>
<n:menu-item clave="magistrados" texto="Magistrados" icono="personas"/>
<n:menu-item clave="distritos" texto="Distritos" icono="documento"/>
