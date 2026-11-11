<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Menú lateral del módulo de Peritajes (EQUIPO 3). Lo incluye layout.tag cuando la pantalla usa modulo="peritajes".
  - clave: la misma que la pantalla pone en <t:layout seccion="...">, para marcarla activa.
  - Sin url = pantalla pendiente (aparece desactivada). Al terminar una pantalla, agregar su url.
  - TODO: con Spring Security, envolver cada opción en <sec:authorize access="hasRole('...')"> según la tabla de roles.
--%>
<n:menu-item clave="inicio" url="/peritajes" texto="Inicio" icono="inicio"/>
<n:menu-item clave="casos" texto="Casos" icono="carpeta"/>
<n:menu-item clave="peritos" url="/peritajes/peritos" texto="Catálogo de peritos" icono="personas"/>
<n:menu-item clave="mis-casos" texto="Mis peritajes" icono="documento"/>
<n:menu-item clave="bitacora" texto="Bitácora de auditoría" icono="bitacora"/>
<n:menu-item clave="panel" texto="Panel de carga de trabajo" icono="grafica"/>
