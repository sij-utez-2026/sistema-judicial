# Instrucciones para asistentes de IA — Sistema Judicial Integral

Este repositorio es **un solo proyecto Spring Boot (MVC + Thymeleaf)** con 5 módulos, cada uno de un equipo:

| Equipo | Módulo | Carpeta |
| :---- | :---- | :---- |
| 1 | Oficialía de Partes | `oficialia/` |
| 2 | Audiencias | `audiencias/` |
| 3 | Peritajes | `peritajes/` |
| 4 | Segunda Instancia | `segunda/` |
| 5 | Copias Certificadas | `copias/` |

Además hay un **núcleo común** (`nucleo/`: login, usuarios, notificaciones, bitácora, archivos, PDF, contratos) que mantienen los integradores. Los integrantes son estudiantes: el código debe ser simple y fácil de entender.

Antes de escribir código o ejecutar comandos de Git:

1. Lee **`docs/FLUJO_DE_TRABAJO.md`** (la sección 8 es obligatoria), **`docs/ARQUITECTURA.md`** y el documento del módulo en `docs/modulos/`. Si hay comunicación con otro módulo, lee **`docs/CONTRATOS.md`**.
2. Ejecuta `git branch --show-current`.
   - Solo se trabaja en ramas personales `equipo-N/<tipo>-<descripcion>`.
   - Si estás en `main`, `develop` o `equipo-N`, **no hagas cambios**: propone crear la rama personal.
3. Modifica **solo** las carpetas del módulo del equipo N. No toques el núcleo, `nucleo/contratos/`, `SecurityConfig`, `pom.xml` ni `application.properties`: explica el cambio y sugiere un issue `contrato`.
4. Solo tecnologías del proyecto: Java 21, Spring Boot 3, Spring MVC, Thymeleaf, Bootstrap 5, Spring Security con sesión, Spring Data JPA, MySQL, Lombok, Jsoup y los servicios del núcleo. No agregues otras.
5. Nunca uses `Repository`, `Service` ni entidades de otro módulo. Usa el núcleo y los contratos.
6. Nunca hagas `push --force`, `rebase` o `reset --hard` en ramas compartidas, ni push, PR o merge sin que la persona lo pida, ni subas `application-local.properties` o contraseñas.
7. Si un requisito no está en el DFR del módulo o está marcado [Confirmar], pregunta; no lo inventes.

Comandos:

- Compilar y probar: `./mvnw verify`
- Ejecutar: `./mvnw spring-boot:run` → `http://localhost:8080`
