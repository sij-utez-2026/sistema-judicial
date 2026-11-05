# Cómo hacer tus pantallas (guía fácil)

Esta guía es para quien nunca ha tocado el front-end de este proyecto. Si sigues los pasos, tu pantalla se verá igual que las de los demás equipos sin que tengas que diseñar nada.

---

## 1. La idea en 30 segundos

Todas las pantallas del sistema comparten un **marco ya hecho**: el menú verde de la izquierda, la barra blanca de arriba, los colores, los botones y los avisos.

**Tú no haces el marco. Solo haces lo que va dentro.**

```text
┌────────────┬────────────────────────────────────┐
│            │  barra de arriba      (ya hecha)   │
│   MENÚ     ├────────────────────────────────────┤
│  VERDE     │                                    │
│ (ya hecho) │   ESTO ES LO ÚNICO QUE HACES TÚ    │
│            │   (títulos, tablas, formularios)   │
│            │                                    │
└────────────┴────────────────────────────────────┘
```

---

## 2. Tu carpeta

Solo trabajas en **tus** carpetas. Cambia `<tu-modulo>` por el nombre de tu módulo:

| Equipo | Nombre de tu módulo |
| :---- | :---- |
| 1 | `oficialia` |
| 2 | `audiencias` |
| 3 | `peritajes` |
| 4 | `segunda` |
| 5 | `copias` |

```text
front-end/
├── WEB-INF/jsp/<tu-modulo>/        ← AQUÍ van tus pantallas
│   ├── inicio.jsp                  ← la pantalla de inicio de tu módulo
│   └── menu.jsp                    ← las opciones del menú verde de tu módulo
└── recursos/<tu-modulo>/           ← AQUÍ van tus estilos e imágenes propios (si los necesitas)
```

**No toques** nada fuera de esas carpetas. Si crees que algo común está mal o falta (por ejemplo, un botón o un color), abre un *issue* en GitHub y avisa a los integradores.

---

## 3. Hacer una pantalla nueva (4 pasos)

Ejemplo: el Equipo 2 quiere una pantalla que muestre la lista de mesas.

### Paso 1: crea el archivo

Dentro de tu carpeta, crea el archivo de la pantalla. Se llama como lo que muestra, en minúsculas:

`front-end/WEB-INF/jsp/audiencias/mesas/lista.jsp`

### Paso 2: copia esta base y cambia lo que dice

```jsp
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="n" tagdir="/WEB-INF/tags/nucleo" %>

<t:layout titulo="Mesas" modulo="audiencias" seccion="mesas">

    <n:migas>Mesas</n:migas>

    <n:encabezado titulo="Mesas de audiencia" descripcion="Mesas por materia y sus abogados.">
        <a class="btn btn-primary" href="<c:url value='/audiencias/mesas/nueva'/>">+ Nueva mesa</a>
    </n:encabezado>

    <n:tarjeta>
        <p>Aquí va el contenido de tu pantalla.</p>
    </n:tarjeta>

</t:layout>
```

Las 4 primeras líneas **siempre se copian igual**. Lo único que cambias en `<t:layout ...>`:

| Dice | Qué poner |
| :---- | :---- |
| `titulo` | El nombre de la pantalla (sale en la pestaña del navegador) |
| `modulo` | El nombre de **tu** módulo (la tabla de arriba) |
| `seccion` | Una palabra para esta pantalla, por ejemplo `mesas`. Sirve para que se ilumine en el menú |

Todo lo que escribas entre `<t:layout>` y `</t:layout>` es el contenido de tu pantalla.

### Paso 3: dile al sistema que la pantalla existe

Esto lo hace el programador de tu equipo en el controller (el archivo Java). Debe devolver la ruta de la pantalla **sin** `.jsp`:

```java
@GetMapping("/audiencias/mesas")
public String listar(Model model) {
    model.addAttribute("mesas", mesaService.listar());
    return "audiencias/mesas/lista";
}
```

Esa ruta (`audiencias/mesas/lista`) es la carpeta de tu pantalla dentro de `jsp/`, sin la extensión.

### Paso 4: ponla en el menú verde

Abre **tu** archivo `menu.jsp`. Ahí ya están las opciones de tu módulo, pero sin enlace (salen en gris, como "Pendiente"). Solo agrega la dirección en la que corresponda:

```jsp
<n:menu-item clave="mesas" url="/audiencias/mesas" texto="Mesas" icono="edificio"/>
```

La palabra de `clave` debe ser **la misma** que pusiste en `seccion` en el paso 2. Así el menú sabe cuál iluminar.

Listo. Ya puedes verla en el navegador (ver sección 6).

---

## 4. Piezas que puedes usar

No inventes estilos: usa estas piezas y todo se verá igual en todos los módulos.

| Para qué | Cómo se escribe |
| :---- | :---- |
| Ruta de arriba ("Mesas / Nueva") | `<n:migas>Mesas</n:migas>` |
| Título grande de la pantalla | `<n:encabezado titulo="..." descripcion="...">` (los botones van adentro) |
| Recuadro blanco | `<n:tarjeta titulo="Datos">...</n:tarjeta>` |
| Etiqueta de estado (Activo, Cancelado…) | `<n:estado valor="Activo"/>` |
| Números de página en una lista | `<n:paginacion pagina="${pagina}" url="/audiencias/mesas"/>` |
| Botón que pregunta "¿Seguro?" antes de hacer algo | `<n:confirmar accion="/audiencias/mesas/3/estado" texto="¿Desactivar la mesa?" boton="Desactivar"/>` |
| Campo para subir un archivo | `<n:archivo nombre="acta" etiqueta="Acta firmada" tipos=".pdf,.jpg" maxMb="5"/>` |
| Un ícono | `<n:icono nombre="buscar"/>` |

**Botones:** `class="btn btn-primary"` es el botón verde principal. `class="btn btn-sij-secundario"` es el botón blanco (para Cancelar).

Para ver cómo se usan todas juntas, abre las pantallas de peritos y copia lo que necesites:
`front-end/WEB-INF/jsp/peritajes/peritos/` (`lista.jsp`, `registro.jsp` y `editar.jsp`).

---

## 5. Las 5 reglas que no se rompen

1. **Todo dato que muestres va dentro de `<c:out>`.** Si escribes `${p.nombre}` suelto, alguien podría meter código malicioso en un nombre.
   - ✅ `<c:out value="${p.nombre}"/>`
   - ❌ `${p.nombre}`
2. **Todos los enlaces llevan `<c:url>`.**
   - ✅ `href="<c:url value='/audiencias/mesas'/>"`
   - ❌ `href="/audiencias/mesas"`
3. **Toda pantalla empieza con `<t:layout>`.** Nunca copies el menú ni la barra de arriba.
4. **No escribas código Java dentro de la pantalla** (nada de `<% ... %>`). Para repetir cosas usa `<c:forEach>` y para condiciones `<c:if>`.
5. **Todo formulario que guarda algo lleva su token de seguridad.** Si usas `<form:form>` (el de Spring) ya lo trae. Si usas un `<form method="post">` normal, agrega `<n:csrf/>` justo debajo.

Tus estilos propios van en `front-end/recursos/<tu-modulo>/css/`, y sus nombres empiezan con las letras de tu módulo (`per-`, `aud-`, `ofi-`, `sin-`, `cop-`) para no chocar con los de otros.

---

## 6. Ver tu pantalla

1. Abre una terminal en la **carpeta principal** del proyecto (la que se llama `sistema-judicial`).
2. Ejecuta:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows: `mvnw.cmd spring-boot:run`
3. Abre <http://localhost:8080> y entra a tu módulo.
4. Cambias algo en tu pantalla, guardas el archivo y **recargas el navegador**. No hace falta reiniciar.

**Si algo se ve raro o sale con el diseño viejo:** detén el programa, ejecuta `./mvnw clean` y vuelve a correrlo.

---

## 7. Si algo no funciona

| Qué ves | Qué hacer |
| :---- | :---- |
| Error 404 al abrir tu pantalla | Revisa que el controller devuelva el nombre correcto (paso 3) y que el archivo `.jsp` esté en tu carpeta |
| Se ve el texto `${algo}` en la pantalla | Falta una de las 4 primeras líneas de la base (las que empiezan con `<%@`) |
| Salen letras raras en los acentos (Ã©) | Falta la primera línea de la base |
| Tu pantalla se ve sin menú o sin colores | Falta el `<t:layout ...>` o no copiaste las 4 primeras líneas |
| No sale tu menú de módulo | En `<t:layout>` falta `modulo="..."` |
| Error al enviar un formulario | Revisa la regla 5 (token de seguridad) |
| Algo común está mal o falta | Abre un *issue* y avisa a los integradores. No lo arregles tú |

---

## 8. Antes de abrir tu PR, revisa

- [ ] Solo cambié archivos de **mi** carpeta.
- [ ] Mi pantalla empieza con `<t:layout>` y usa las piezas de la sección 4.
- [ ] Todo dato va dentro de `<c:out>` y todo enlace dentro de `<c:url>`.
- [ ] La probé en el navegador.
- [ ] Mi opción del menú (`menu.jsp`) ya tiene su dirección.

---

**¿Quieres más detalle?** Está en `front-end/README.md` (con todos los ejemplos) y en `docs/ARQUITECTURA.md`, sección 16.
