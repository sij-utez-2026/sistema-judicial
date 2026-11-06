<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Menú lateral del módulo de Oficialía de Partes (EQUIPO 1). Lo incluye layout.tag cuando la pantalla usa modulo="oficialia".
  - clave: la misma que la pantalla pone en <t:layout seccion="...">, para marcarla activa.
  - Sin url = pantalla pendiente (aparece desactivada). Al terminar una pantalla, agregar su url.
  - TODO: con Spring Security, envolver cada opción en <sec:authorize access="hasRole('...')"> según la tabla de roles.
--%>
<n:menu-item clave="inicio" url="/oficialia" texto="Inicio" icono="inicio"/>
<n:menu-item clave="demandas" texto="Demandas" icono="carpeta"/>
<n:menu-item clave="nueva-demanda" texto="Registrar demanda" icono="mas"/>
<n:menu-item clave="usuarios" texto="Usuarios de la oficialía" icono="personas"/>
<n:menu-item clave="materias" texto="Materias" icono="documento"/>
<n:menu-item clave="distritos" texto="Distritos y municipios" icono="edificio"/>
<n:menu-item clave="juzgados" texto="Juzgados y oficialías" icono="balanza"/>
