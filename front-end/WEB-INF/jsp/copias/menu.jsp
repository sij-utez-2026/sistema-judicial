<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>
<%--
  Menú lateral del módulo de Copias Certificadas (EQUIPO 5). Lo incluye layout.tag cuando la pantalla usa modulo="copias".
  - clave: la misma que la pantalla pone en <t:layout seccion="...">, para marcarla activa.
  - TODO: con Spring Security, envolver cada grupo en <sec:authorize access="hasRole('...')">:
      Trabajo diario → COPIAS_PERSONAL · Administración → COPIAS_ADMIN
--%>
<n:menu-item clave="inicio" url="/copias" texto="Inicio" icono="inicio"/>
<n:menu-item clave="nueva" url="/copias/solicitudes/nueva" texto="Nueva solicitud" icono="mas"/>
<n:menu-item clave="solicitudes" url="/copias/solicitudes" texto="Registro de solicitudes" icono="buscar"/>
<n:menu-item clave="validacion" url="/copias/validacion" texto="Validación de privadas" icono="documento"/>
<n:menu-item clave="entregas" url="/copias/entregas" texto="Entregas" icono="carpeta"/>

<div class="sij-etiqueta-menu">Administración del área</div>
<n:menu-item clave="personal" url="/copias/admin/personal" texto="Trabajadores" icono="personas"/>
<n:menu-item clave="colores" url="/copias/admin/colores" texto="Colores" icono="editar"/>
