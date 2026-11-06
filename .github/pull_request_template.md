## Qué es este PR
- [ ] Tarea personal → `equipo-N`
- [ ] Sincronización `develop` → `equipo-N`
- [ ] Integración `equipo-N` → `develop`
- [ ] Versión `develop` → `main`

## Equipo, módulo y requerimientos
- Equipo / módulo: <!-- equipo-3 / peritajes -->
- RF del DFR: <!-- 3.1, 3.2 -->
- Issues: <!-- "Relacionado con #12" en PR a equipo-N · "Closes #12" en PR a develop -->

## Qué cambia

## Cómo probarlo
<!-- usuario de prueba, URL y pasos -->

## Checklist
- [ ] Traje lo último de la rama destino y no hay conflictos
- [ ] `./mvnw verify` pasa y lo probé en el navegador
- [ ] Solo modifiqué archivos de mi módulo (si no, lo explico abajo)
- [ ] No uso Repository, Service ni entidades de otro módulo
- [ ] URLs con `/<mi-prefijo>/`, tablas con `<pre>_`, `@PreAuthorize` en controllers
- [ ] Mis JSP están en `front-end/WEB-INF/jsp/<mi-prefijo>/`, usan `<t:layout modulo="...">` y los componentes `<n:...>`, y muestran los datos con `<c:out>`
- [ ] Si renombré o quité atributos de una entidad, lo aviso aquí
- [ ] Actualicé `docs/modulos/<mi-modulo>.md` si cambié rutas, tablas o contratos
- [ ] No subí `application-local.properties`, contraseñas, `target/` ni `.idea/`

## Archivos fuera de mi módulo (si aplica)