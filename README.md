# Sistema Judicial Integral (SIJ)

Proyecto integrador de la UTEZ: **un solo sistema judicial** con 5 módulos, uno por equipo, dentro de un solo proyecto **Spring Boot (MVC)** con pantallas **JSP**.

| Equipo | Módulo | URL | Estado |
| :---- | :---- | :---- | :---- |
| 1 | Oficialía de Partes | `/oficialia` | Página de inicio y menú de ejemplo |
| 2 | Audiencias (SGCA) | `/audiencias` | Página de inicio y menú de ejemplo |
| 3 | Peritajes (SGPJ) | `/peritajes` | Catálogo de peritos (RF 3.1 – 3.4), con datos en memoria |
| 4 | Segunda Instancia (SSI) | `/segunda` | Página de inicio y menú de ejemplo |
| 5 | Copias Certificadas (SGCC) | `/copias` | Página de inicio y menú de ejemplo |
| Integradores | Núcleo común | `/` | Plantilla, componentes y estilos comunes listos. Falta login, usuarios, base de datos y lo demás del núcleo |

## Correr el proyecto

**Requisitos:** Java 21 o superior. Por ahora **no hace falta MySQL**: `application.properties` desactiva la base de datos y Peritajes guarda en memoria (se reinicia al apagar).

Desde la **carpeta raíz** del repositorio:

```bash
./mvnw spring-boot:run            # Windows: mvnw.cmd spring-boot:run
```

Abre <http://localhost:8080>. Es la página de inicio con una tarjeta por módulo.

| Pantalla | URL |
| :---- | :---- |
| Inicio del sistema | <http://localhost:8080/> |
| Inicio de cada módulo | `/oficialia`, `/audiencias`, `/peritajes`, `/segunda`, `/copias` |
| Catálogo de peritos | <http://localhost:8080/peritajes/peritos> |
| Registrar perito | <http://localhost:8080/peritajes/peritos/nuevo> |

- **Desde IntelliJ:** ejecuta `IntegradoraApplication`. El *Working directory* debe ser la carpeta raíz del repositorio, porque de ahí se lee `front-end/`.
- **`./mvnw: Permission denied`:** ejecuta `chmod +x mvnw`.
- **Una pantalla sale con el diseño viejo:** ejecuta `./mvnw clean` y vuelve a correrlo.

Compilar y correr las pruebas: `./mvnw verify`. Lo mismo corre el CI en cada PR.

## Cómo está organizado

```text
sistema-judicial/
├── front-end/                     # PANTALLAS (JSP). Guía: front-end/README.md
│   ├── WEB-INF/tags/              #   layout.tag (menú lateral), layout-publico.tag y componentes <n:...>
│   ├── WEB-INF/jsp/<prefijo>/     #   pantallas de cada módulo + su menu.jsp
│   └── recursos/<prefijo>/        #   CSS y JS (los comunes en recursos/nucleo/)
├── src/main/java/mx/edu/utez/
│   ├── integradora/               #   IntegradoraApplication (arranque) y ServletInitializer
│   └── sij/
│       ├── nucleo/                #   INTEGRADORES: config, inicio (/), datos comunes de las vistas
│       ├── oficialia/  audiencias/  peritajes/  segunda/  copias/   # un paquete por equipo
├── src/main/resources/
│   └── application.properties     #   configuración (solo integradores)
├── docs/                          # Arquitectura, contratos, flujo de Git y un documento por módulo
├── AGENTS.md                      # Reglas para asistentes de IA (Copilot, Claude, ChatGPT…)
└── pom.xml                        # Empaquetado War (lo exige JSP)
```

**Tecnologías:** Java 21, Spring Boot 4.1 (Spring MVC), JSP + JSTL, Bootstrap 5 (CDN), Bean Validation. Más adelante: Spring Security, Spring Data JPA con MySQL 8 y Thymeleaf solo para plantillas de PDF y correo.

## Pantallas: todas con la misma estructura

Todas las pantallas usan la plantilla común `<t:layout>`. Así los cinco módulos comparten el mismo menú lateral, encabezado, mensajes y estilos, y cada equipo solo escribe el contenido de su pantalla:

```jsp
<t:layout titulo="Mesas" modulo="audiencias" seccion="mesas">
    <n:migas>Mesas</n:migas>
    <n:encabezado titulo="Mesas de audiencia" descripcion="Mesas por materia y sus abogados."/>
    <n:tarjeta> ... </n:tarjeta>
</t:layout>
```

Cada equipo agrega las opciones de su menú lateral en `front-end/WEB-INF/jsp/<prefijo>/menu.jsp`. El paso a paso y la lista de componentes están en **[front-end/README.md](front-end/README.md)**.

## Documentación

| Documento | Para qué |
| :---- | :---- |
| [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md) | Arquitectura común: módulos, núcleo, seguridad, base de datos y front-end (sección 16) |
| [docs/CONTRATOS.md](docs/CONTRATOS.md) | Interfaces de Java entre módulos |
| [docs/GUIA_PANTALLAS.md](docs/GUIA_PANTALLAS.md) | **Empieza aquí:** cómo hacer tus pantallas, explicado fácil |
| [docs/FLUJO_DE_TRABAJO.md](docs/FLUJO_DE_TRABAJO.md) | Reglas obligatorias de Git y para asistentes de IA |
| [docs/GUIA_GITHUB.md](docs/GUIA_GITHUB.md) | Tutorial de GitHub paso a paso |
| [docs/modulos/](docs/modulos/) | Un documento por módulo (a partir de su DFR) y la plantilla `_PLANTILLA_MODULO.md` |
| [front-end/README.md](front-end/README.md) | Cómo hacer una pantalla nueva |
| [AGENTS.md](AGENTS.md) | Instrucciones para asistentes de IA |

## Cómo se trabaja

```text
main  ←  develop  ←  equipo-N  ←  equipo-N/<tipo>-<descripcion>
```

1. Crea tu rama desde la de tu equipo: `git switch equipo-3 && git pull && git switch -c equipo-3/feat-mi-tarea`.
2. Modifica **solo** las carpetas de tu módulo: tu paquete Java, `front-end/WEB-INF/jsp/<prefijo>/` y `front-end/recursos/<prefijo>/`.
3. `./mvnw verify`, pruébalo en el navegador y abre un PR hacia `equipo-N`.

Detalle en [docs/FLUJO_DE_TRABAJO.md](docs/FLUJO_DE_TRABAJO.md).

## Pendientes del núcleo (integradores)

- Spring Security: login, roles, `@PreAuthorize` y el menú según el rol.
- Base de datos MySQL con `application-local.properties` (hoy desactivada).
- Usuarios, notificaciones, bitácora, archivos y PDF.
- Las interfaces de `docs/CONTRATOS.md` en `nucleo/contratos/`.

El avance completo del Sprint 0 está en `docs/ARQUITECTURA.md`, sección 14.
