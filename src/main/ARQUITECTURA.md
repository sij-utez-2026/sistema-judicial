# Arquitectura del Sistema Judicial Integral

Versión 4.0 · 24/10/2026 · Basada en los DFR de los cinco módulos

> **Nombre provisional.** No se tiene el nombre oficial del sistema completo. Aquí se usa **"Sistema Judicial Integral" (SIJ)**, el repositorio `sistema-judicial` y el paquete Java `mx.edu.utez.sij`. Cambiarlos antes de crear el repo.
>
> **Marcas que se usan en todos los documentos:**
> - **[Propuesta]**: idea de este documento que deben aprobar los integradores (un representante por equipo + líder).
> - **[Confirmar]**: algo que los DFR no dicen o dicen distinto entre sí. Lo resuelve el equipo dueño con el cliente.
>
> **Criterio:** tecnologías básicas vistas en la carrera (Java, Spring Boot MVC, Thymeleaf y una base de datos relacional).

---

## Índice

1. Qué es este proyecto
2. Módulos y equipos
3. Recorrido de una demanda por el sistema
4. Tecnologías
5. Cómo funciona una petición (MVC)
6. Arquitectura general
7. El núcleo común
8. Reglas para que los módulos convivan
9. Contratos entre módulos
10. Seguridad
11. Base de datos
12. Árbol del repositorio
13. GitHub
14. Sprint 0
15. Decisiones pendientes entre equipos

---

## 1. Qué es este proyecto

Un solo sistema judicial con **5 módulos**, uno por equipo, dentro de **un solo proyecto Spring Boot y un solo repositorio de GitHub**. Todos los módulos comparten:

- el mismo inicio de sesión,
- la misma base de datos,
- el mismo diseño de pantallas.

Se comunican entre sí por **contratos** (interfaces de Java). Así, al final no hay que "unir" nada.

| Documento | Para qué |
| :---- | :---- |
| `docs/ARQUITECTURA.md` | Lo común a todo el sistema (este documento) |
| `docs/CONTRATOS.md` | Las interfaces exactas entre módulos |
| `docs/modulos/<modulo>.md` | Lo propio de cada módulo, a partir de su DFR |
| `docs/FLUJO_DE_TRABAJO.md` | Reglas de Git y reglas para asistentes de IA |
| `docs/GUIA_GITHUB.md` | Tutorial paso a paso de GitHub para todos los integrantes |

---

## 2. Módulos y equipos

| Equipo | Módulo | Nombre en su DFR | Líder (según DFR) | Prefijo |
| :---- | :---- | :---- | :---- | :---- |
| Equipo 1 | Oficialía de Partes | Sistema de Gestión y Recepción de Demandas para Oficialías de Partes | Flores Pérez Valeria Jatziry | `oficialia` / `ofi_` |
| Equipo 2 | Audiencias | SGCA – Sistema de Gestión y Control de Audiencias | Hernández Bautista Jimena | `audiencias` / `aud_` |
| Equipo 3 | Peritajes | SGPJ – Sistema para Gestión de Peritajes Judiciales | Contreras Carbajal Marco Antonio | `peritajes` / `per_` |
| Equipo 4 | Segunda Instancia | SSI – Sistema de Gestión de Tocas de Segunda Instancia | Modesto Cruz Bastián Azael | `segunda` / `sin_` |
| Equipo 5 | Copias Certificadas | SGCC – Sistema de Gestión de Copias Certificadas | Sánchez Muñoz Emilio | `copias` / `cop_` |
| Integradores | Núcleo común | — | Un representante por equipo + líder general: **Contreras Carbajal Marco Antonio** | `nucleo` / `core_` |

Cliente de los cinco módulos: **Narváez Figueroa José Christian**.

El **prefijo** se usa siempre igual:

| Dónde | Regla | Ejemplo |
| :---- | :---- | :---- |
| Paquete Java | `mx.edu.utez.sij.<prefijo>` | `mx.edu.utez.sij.audiencias` |
| URL | `/<prefijo>/...` | `/audiencias/agenda` |
| Vistas | `templates/<prefijo>/` | `templates/audiencias/agenda.html` |
| Tablas | `<pre>_<nombre>` | `aud_audiencia` |
| Roles | `<PREFIJO>_<ROL>` | `AUDIENCIAS_ABOGADO` |

---

## 3. Recorrido de una demanda por el sistema

Así se conectan los cinco DFR. Cada paso dice qué módulo lo hace y con qué requerimiento (RF).

```mermaid
flowchart LR
    subgraph OFI["1 · Oficialía de Partes"]
        O1["Registrar demanda<br/>y revisar duplicidad<br/>(RF 3.1)"]
        O2["Dictaminar procedencia<br/>(función 2.2) [Confirmar RF]"]
        O3["Elegir mesa y abogado<br/>y enviar a Audiencias<br/>(RF 5.1)"]
        O4["Escalar a segunda instancia<br/>y asignar sala<br/>(RF 4.1, 5.2)"]
    end
    subgraph AUD["2 · Audiencias"]
        A1["Recibir demanda<br/>(RF 4.1)"]
        A2["Programar y llevar<br/>audiencias, actas<br/>(RF 5.1–5.8)"]
        A3["Registrar resolución<br/>(RF 5.9)"]
    end
    subgraph PER["3 · Peritajes"]
        P1["Asignar perito por<br/>materia y carga<br/>(RF 2.1–2.2)"]
        P2["Dictamen entregado<br/>(RF 4.3–4.4)"]
    end
    subgraph SIN["4 · Segunda Instancia"]
        S1["Recibir toca<br/>(RF 5.1)"]
        S2["Ponencias, consulta del<br/>expediente y resolución<br/>(RF 5.2, 6.1, 7.1)"]
    end
    subgraph COP["5 · Copias Certificadas"]
        C1["Solicitud pública o privada,<br/>validación, pago simulado<br/>y entrega (RF 2.1–5.1)"]
    end

    O1 --> O2 --> O3 --> A1 --> A2
    A2 -- "solicitar perito (RF 6.1)" --> P1 --> P2
    P2 -- "consultar dictamen (RF 6.3)" --> A2
    A2 --> A3
    A3 -- "demanda Resuelta" --> O4
    O4 --> S1 --> S2
    S2 -. "lee demanda, audiencias y dictámenes" .-> OFI & AUD & PER
    C1 -. "verifica expediente, tipo y partes" .-> OFI
```

**Primera instancia**

1. **Oficialía** registra la demanda y el sistema busca posibles duplicados. El personal del juzgado decide si es procedente o improcedente.
2. **Oficialía** pide a Audiencias las mesas de la materia con sus abogados, elige uno y le envía la demanda.
3. **Audiencias** recibe la demanda en el listado del abogado y programa las audiencias que hagan falta (con su acta descargable y su acta firmada).
4. **Audiencias** puede pedir un perito del sistema. **Peritajes** asigna al perito de menor carga, y cuando el perito entrega el dictamen, Audiencias lo consulta.
5. **Audiencias** registra la resolución y la demanda queda finalizada en primera instancia.

**Segunda instancia**

6. Si hay inconformidad, **Oficialía** marca la demanda "En Segunda Instancia" y le asigna una sala (que administra Segunda Instancia).
7. **Segunda Instancia** genera la toca. El magistrado registra ponencias, consulta el expediente completo de los otros tres módulos y emite su resolución.

**En cualquier momento**

8. Cualquier persona puede pedir **copias certificadas** en un portal público, sin cuenta:
   - si la demanda es **pública**, pasa directo al pago (simulado);
   - si es **privada**, el personal del juzgado valida que el solicitante sea parte de la demanda.

   Después el juez o el personal del juzgado registra la entrega física.

---

## 4. Tecnologías

| Para qué | Tecnología | Nota |
| :---- | :---- | :---- |
| Lenguaje | **Java 21** | |
| Framework | **Spring Boot 3.x** con **Spring MVC** | Un solo proyecto Maven |
| Pantallas | **Thymeleaf** + **Bootstrap 5** | HTML generado en el servidor; Bootstrap por CDN |
| Base de datos | **MySQL 8** [Propuesta] | Cualquier relacional sirve; solo cambian el driver y la URL |
| Acceso a datos | **Spring Data JPA** (Hibernate) | |
| Seguridad | **Spring Security** con formulario y sesión | |
| Validaciones | **Bean Validation** | `@NotBlank`, `@Email`, `@Size`, `@Pattern` |
| Correo | **Spring Mail** (SMTP) | Recuperación de contraseña y avisos. Apagado en desarrollo |
| PDF | **OpenHTMLtoPDF** [Propuesta] | Solo dentro del núcleo (`PdfService`). Convierte una plantilla Thymeleaf en PDF. Lo usan Audiencias (acta) y Copias (papeleta y comprobante) |
| Menos código repetido | **Lombok** | Opcional |
| Limpiar HTML | **Jsoup** | Solo para el texto del editor del dictamen (Peritajes) |
| Editor de texto | **Quill** por CDN | Solo Peritajes (dictamen) |
| Calendario | **FullCalendar** por CDN [Propuesta] | Solo Audiencias (agenda). Opcional: también sirve una tabla por día |
| Pruebas | **JUnit 5** | Viene con Spring Boot |

**Dependencias de Spring Initializr:** Spring Web, Thymeleaf, Spring Security, Spring Data JPA, MySQL Driver, Validation, Java Mail Sender, Lombok y Spring Boot DevTools. A mano en el `pom.xml`: `thymeleaf-extras-springsecurity6`, `jsoup` y OpenHTMLtoPDF.

**No se usa:** Vue/React/Angular, JWT, Docker, Flyway, microservicios, API REST entre módulos ni pasarelas de pago reales.

> **Sobre las "API REST" de los DFR.** Varios DFR dicen que los módulos se comunican "mediante API REST con JSON". Como todos viven en el mismo proyecto, esa comunicación se hace con **interfaces de Java** (sección 9), que son más simples y no necesitan servidores separados. En cada documento de módulo está el texto sugerido para corregir su DFR.

---

## 5. Cómo funciona una petición (MVC)

```mermaid
sequenceDiagram
    autonumber
    participant N as Navegador
    participant S as Spring Security
    participant C as Controller
    participant SV as Service
    participant R as Repository
    participant BD as MySQL
    participant V as Vista Thymeleaf

    N->>S: GET /audiencias/expedientes
    S->>S: ¿Hay sesión? ¿Tiene el rol?
    S->>C: ExpedienteController.listar()
    C->>SV: expedienteService.misExpedientes(filtros)
    SV->>R: expedienteRepository.findBy...()
    R->>BD: SELECT ...
    BD-->>SV: datos
    SV-->>C: lista
    C->>V: model.addAttribute(...) → "audiencias/expedientes/lista"
    V-->>N: HTML
```

| Capa | Paquete | Qué hace | Qué NO hace |
| :---- | :---- | :---- | :---- |
| Controller | `controller` | Recibe la petición, valida el formulario, llama al service y elige la vista | Reglas de negocio ni consultas |
| Service | `service` | Reglas de negocio del DFR, `@Transactional` | Saber de HTML |
| Repository | `repository` | Interfaces `JpaRepository` | Reglas |
| Model | `model` | Entidades `@Entity` y `enum` de estados | — |
| DTO | `dto` | Formularios y datos para las vistas | — |
| Vista | `templates/<prefijo>/` | HTML con Thymeleaf | Lógica |

---

## 6. Arquitectura general

**Monolito modular:** una aplicación Spring Boot dividida en paquetes por módulo.

```mermaid
flowchart TB
    PUB["Ciudadanos<br/>(sin cuenta)"]
    U["Personal con cuenta<br/>oficialía, juzgado, abogados,<br/>peritos, magistrados, admins"]

    subgraph APP["Aplicación Spring Boot"]
        SEC["Spring Security<br/>sesión · roles · rutas públicas /copias/publico/**"]
        subgraph NUC["NÚCLEO (integradores)"]
            N1["usuarios, roles, login,<br/>recuperación y bloqueo"]
            N2["notificaciones + correo"]
            N3["bitácora"]
            N4["archivos"]
            N5["PDF · layout · errores"]
        end
        M1["oficialia<br/>(catálogos judiciales, expediente)"]
        M2["audiencias"]
        M3["peritajes"]
        M4["segunda"]
        M5["copias"]
    end

    BD[("MySQL · un esquema<br/>core_ ofi_ aud_ per_ sin_ cop_")]

    PUB -- HTTPS --> SEC
    U -- HTTPS --> SEC
    SEC --> M1 & M2 & M3 & M4 & M5
    M1 & M2 & M3 & M4 & M5 --> NUC
    M1 -. "MesaConsultaApi · RecepcionDemandaApi" .-> M2
    M1 -. "SalaConsultaApi · RecepcionTocaApi" .-> M4
    M2 -. "SolicitudPeritajeApi · DictamenConsultaApi" .-> M3
    M2 & M3 & M4 & M5 -. "ExpedienteApi · CatalogoJudicialApi" .-> M1
    M4 -. "AudienciaConsultaApi" .-> M2
    M4 -. "DictamenConsultaApi" .-> M3
    APP --> BD
```

Cada flecha punteada es un contrato. Va **del módulo que lo usa al que lo implementa** (detalle en `docs/CONTRATOS.md`).

---

## 7. El núcleo común (`nucleo`)

Lo que **todos** los DFR piden se hace una sola vez. Ningún módulo hace su propio login, su tabla de usuarios, sus correos, su bitácora, su subida de archivos ni su generación de PDF.

| Servicio | Qué ofrece | Lo piden |
| :---- | :---- | :---- |
| Control de acceso | Login, cerrar sesión, recuperar y cambiar contraseña, bloqueo por intentos | Los 5 DFR (RF 1.x) |
| `UsuarioService` | Crear usuarios, dar y quitar roles, desactivar y reactivar | Todos los que dan de alta personal |
| `SesionService` | Usuario en sesión: id, nombre, roles | Todos |
| `NotificacionService` | Aviso en la campana + correo | Peritajes, Segunda Instancia (los demás lo pospusieron) |
| `BitacoraService` | Registro inalterable con usuario, rol, acción, entidad, fecha e IP | Peritajes 5.2, Audiencias 7.1–7.2, Segunda 6.2 |
| `ArchivoService` | Subir con validación de tipo y tamaño, descargar con permiso | Todos |
| `PdfService` | Plantilla Thymeleaf → PDF descargable | Audiencias 5.7, Copias 2.3 y 4.1 |
| Layout | Menú por rol, campana, mensajes, diseño institucional, layout público | Todos |

### 7.1 Control de acceso unificado

Los cinco DFR describen su propio login con reglas parecidas pero no iguales. Como el login es uno solo, se unifican así:

| Regla | Valor unificado | De dónde sale |
| :---- | :---- | :---- |
| Datos de entrada | Correo electrónico y contraseña | Todos (Copias también menciona "usuario"; se usa el correo) |
| Quién entra | Solo usuarios registrados y **activos** | Todos |
| Mensajes | "Credenciales incorrectas" · "Cuenta inactiva" · "Cuenta bloqueada temporalmente" | Audiencias 1.1 |
| Intentos fallidos | **3 intentos → bloqueo de 15 minutos** | Audiencias, Segunda, Copias |
| Recuperar contraseña | Enlace por correo, **vigente 15 minutos y de un solo uso** | Audiencias 1.2, Copias 1.3, Segunda 1.2 |
| Mensaje si el correo no existe | **Mismo mensaje en ambos casos:** "Si el correo está registrado, recibirás las instrucciones" [Propuesta] | Segunda 1.2 lo pide así por seguridad; Audiencias y Copias piden decir que no existe (ver sección 15) |
| Nueva contraseña | Mínimo **8 caracteres**, una mayúscula, una minúscula y un número; **distinta a la actual**; confirmación igual | Audiencias 1.3, Copias 1.3 |
| Cerrar sesión | Invalida la sesión. El botón "Atrás" no muestra páginas privadas | Audiencias 1.4, Copias 1.2 |
| Sesión inactiva | **30 minutos** por defecto; **configurable por rol** (`SEGUNDA_MAGISTRADO` = 8 horas) [Propuesta] | Audiencias 1.4; Segunda pide sesión extendida por las ponencias |
| Primer inicio de sesión | Si la cuenta se creó con contraseña temporal, obliga a cambiarla | Peritajes, Segunda 4.1 |
| Ciudadanos | **No tienen cuenta**: usan el portal público de Copias | Decisión del grupo |

En desarrollo el correo está apagado: el enlace de recuperación y la contraseña temporal **se imprimen en la consola** para poder probar.

### 7.2 Roles

Un usuario puede tener roles de varios módulos. En el menú solo ve los módulos de sus roles.

| Rol | Módulo | Quién es (según su DFR) |
| :---- | :---- | :---- |
| `SISTEMA_ADMIN` | Núcleo / Oficialía | **Administrador Mayor** de Oficialía: configura materias, distritos, juzgados y oficialías, y da de alta a los administradores de cada módulo [Propuesta] |
| `OFICIALIA_PERSONAL` | Oficialía | Personal de Oficialía de Partes |
| `OFICIALIA_REVISOR` | Oficialía | Personal del juzgado / revisor de procedencia |
| `AUDIENCIAS_ADMIN` | Audiencias | Administrador de audiencias |
| `AUDIENCIAS_ABOGADO` | Audiencias | Abogado de audiencia (equivalente al juez) |
| `PERITAJES_ADMIN` | Peritajes | Administrador |
| `PERITAJES_PERITO_ADMIN` | Peritajes | Perito Administrador |
| `PERITAJES_PERITO` | Peritajes | Perito |
| `SEGUNDA_ADMIN` | Segunda Instancia | Administrador de salas y magistrados |
| `SEGUNDA_MAGISTRADO` | Segunda Instancia | Magistrado |
| `COPIAS_PERSONAL` | Copias | Personal administrativo del juzgado (visto bueno) |
| `COPIAS_JUEZ` | Copias | Juez / autoridad (registra la entrega) |

### 7.3 Quién da de alta a quién

| Quién | Crea usuarios con rol | Dónde (según su DFR) |
| :---- | :---- | :---- |
| `SISTEMA_ADMIN` | Administradores de cada módulo, `COPIAS_PERSONAL`, `COPIAS_JUEZ` y el primer `OFICIALIA_PERSONAL` de cada oficialía | `/admin/usuarios` (núcleo) |
| `OFICIALIA_PERSONAL` | `OFICIALIA_PERSONAL` y `OFICIALIA_REVISOR` **solo de su propia oficialía** | Oficialía RF 2.3 |
| `AUDIENCIAS_ADMIN` | `AUDIENCIAS_ABOGADO` | Audiencias RF 3.1 |
| `PERITAJES_ADMIN` / `PERITAJES_PERITO_ADMIN` | `PERITAJES_PERITO` | Peritajes RF 3.1 |
| `SEGUNDA_ADMIN` | `SEGUNDA_MAGISTRADO` | Segunda RF 4.1 |

Cada módulo crea las cuentas con `usuarioService.crear(...)` del núcleo. Solo puede dar **roles de su propio módulo**. Al dar de baja a alguien en un módulo se le quita ese rol, y si ya no le queda ninguno, su cuenta se desactiva.

### 7.4 Bitácora

Una sola tabla (`core_bitacora`) con columna `modulo`. Cada módulo muestra solo sus registros.

- **Datos de cada registro:**
  - fecha y hora del servidor,
  - módulo,
  - usuario (id y nombre),
  - rol,
  - acción,
  - entidad y su id,
  - detalle,
  - IP,
  - id de sesión.

  Son los seis datos obligatorios de Audiencias 7.1, más el módulo.
- **Inalterable:** no existen métodos ni pantallas para editar o borrar registros.
- **Si no se puede registrar, la acción no se completa** (Audiencias 7.1). `bitacoraService.registrar(...)` se llama dentro del mismo `@Transactional` del service: si falla, se revierte todo.
- **Llamadas entre módulos:** cuando un módulo actúa por otro (por ejemplo, Oficialía envía una demanda a Audiencias), se registra con el usuario en sesión y en el detalle `origen: OFICIALIA`.

### 7.5 Archivos

`archivoService.guardar(modulo, archivo, tiposPermitidos, tamanoMaximoMB)`:
- valida la extensión y el tipo del archivo,
- guarda el archivo con un nombre aleatorio fuera de `static/`,
- y devuelve el id de `core_archivo`.

Cada módulo define sus propios límites:

| Módulo | Archivo | Tipos | Tamaño máximo |
| :---- | :---- | :---- | :---- |
| Oficialía | Documentos de la demanda | [Confirmar] (propuesta: PDF, JPG, PNG) | [Confirmar] (propuesta: 10 MB) |
| Audiencias | Acta firmada | PDF o imagen | 5 MB |
| Peritajes | Documentos del dictamen | PDF, Word, PNG, JPEG | 25 MB |
| Segunda Instancia | Apelación y resolución | PDF | [Confirmar] (propuesta: 10 MB) |
| Copias | Identificación oficial (INE) | JPG, PNG, PDF | [Confirmar] (propuesta: 5 MB) |

**Para descargar** se usa siempre una ruta del módulo que muestra el archivo. Esa ruta revisa el permiso y luego llama a `archivoService.descargar(id)`. No existe una ruta pública de archivos.

### 7.6 Rutas del núcleo

| Método | URL | Quién | Qué hace |
| :---- | :---- | :---- | :---- |
| GET / POST | `/login` | Público | Iniciar sesión |
| POST | `/logout` | Con sesión | Cerrar sesión |
| GET / POST | `/recuperar` | Público | Pedir enlace de recuperación |
| GET / POST | `/recuperar/{token}` | Público | Escribir la nueva contraseña |
| GET / POST | `/cuenta/password` | Con sesión | Cambiar contraseña |
| GET | `/` | Con sesión | Inicio con una tarjeta por módulo al que tiene acceso |
| GET | `/notificaciones` | Con sesión | Sus notificaciones |
| POST | `/notificaciones/{id}/leida` | Con sesión | Marcar como leída |
| GET / POST | `/admin/usuarios/**` | `SISTEMA_ADMIN` | Administración general de usuarios |

### 7.7 Tablas del núcleo

```mermaid
erDiagram
    CORE_USUARIO ||--o{ CORE_USUARIO_ROL : tiene
    CORE_USUARIO ||--o{ CORE_TOKEN_RECUPERACION : solicita
    CORE_USUARIO ||--o{ CORE_NOTIFICACION : recibe
    CORE_USUARIO ||--o{ CORE_BITACORA : realiza
    CORE_USUARIO ||--o{ CORE_ARCHIVO : sube

    CORE_USUARIO {
        bigint id PK
        varchar nombre
        varchar apellidos
        varchar correo UK
        varchar password_hash
        boolean activo
        boolean debe_cambiar_password
        int intentos_fallidos
        datetime bloqueado_hasta
        datetime creado_en
    }
    CORE_USUARIO_ROL {
        bigint usuario_id FK
        varchar rol
    }
    CORE_TOKEN_RECUPERACION {
        bigint id PK
        bigint usuario_id FK
        varchar token_hash
        datetime expira_en
        boolean usado
    }
    CORE_NOTIFICACION {
        bigint id PK
        bigint usuario_id FK
        varchar modulo
        varchar titulo
        varchar mensaje
        varchar enlace
        boolean leida
        datetime creada_en
    }
    CORE_BITACORA {
        bigint id PK
        datetime fecha
        varchar modulo
        bigint usuario_id FK
        varchar usuario_nombre
        varchar rol
        varchar accion
        varchar entidad
        varchar entidad_id
        text detalle
        varchar ip
        varchar sesion_id
    }
    CORE_ARCHIVO {
        bigint id PK
        varchar modulo
        varchar nombre_original
        varchar nombre_guardado
        varchar tipo
        bigint tamano_bytes
        bigint subido_por FK "null si lo subió un ciudadano"
        datetime subido_en
    }
```

Las **materias, distritos, municipios y juzgados** no están en el núcleo. Son de Oficialía, porque su DFR (RF 2.1 y 2.2) dice que el Administrador Mayor los configura. Los demás módulos los consultan con `CatalogoJudicialApi`.

---

## 8. Reglas para que los módulos convivan

1. **Paquete:** todo tu código Java va en `mx.edu.utez.sij.<prefijo>`.
2. **URLs:** todas tus rutas empiezan con `/<prefijo>/`. Solo Copias tiene rutas públicas, en `/copias/publico/**`.
3. **Vistas** en `templates/<prefijo>/` y estáticos en `static/<prefijo>/`.
4. **Tablas:** `@Table(name = "<pre>_<nombre>")`.
5. **Nunca** uses un `Repository`, `Service` o entidad de otro módulo. Solo el núcleo y los contratos (`docs/CONTRATOS.md`).
6. **Relaciones entre tablas:**
   - Con tablas `core_*`, sí.
   - Con otro módulo, no: guarda solo su identificador (por ejemplo, `numero_expediente` o `materia_id`) como columna simple, sin `@ManyToOne`.
7. **Identificador común:** el **número de expediente** que genera Oficialía. Todos los módulos lo llaman `numeroExpediente` en Java y `numero_expediente` en la base.
8. **Permisos:** `@PreAuthorize` en cada controller. Nadie edita `SecurityConfig`.
9. **Formularios:** `GET` para ver y `POST` para guardar, cambiar o borrar.
10. **Mensajes al usuario:** `RedirectAttributes.addFlashAttribute("exito" | "error", texto)`, con los textos exactos de los criterios de aceptación del DFR.
11. **Errores de contratos:** todos los errores de negocio extienden `ReglaNegocioException` (núcleo). Quien llama un contrato atrapa esa excepción y muestra `e.getMessage()`.
12. **Fechas:** `LocalDate` / `LocalDateTime` con la zona `America/Mexico_City`.
13. **Paginación:** `Pageable` con el tamaño que diga cada DFR (Audiencias 10, Peritajes 25).
14. **Nombres en español**, sin acentos en el código.

---

## 9. Contratos entre módulos

Un **contrato** es una interfaz de Java en `nucleo/contratos/<modulo>/`. El módulo dueño la implementa y los demás la usan por inyección (`private final MesaConsultaApi mesaConsultaApi;`). El detalle completo, con métodos y datos, está en **`docs/CONTRATOS.md`**.

| Contrato | Lo implementa | Lo usa | Para qué (RF) |
| :---- | :---- | :---- | :---- |
| `CatalogoJudicialApi` | Oficialía | Todos | Materias, distritos y juzgados (Oficialía 2.1, 2.2; Segunda 2.1) |
| `ExpedienteApi` | Oficialía | Audiencias, Peritajes, Segunda, Copias | Datos de la demanda, partes, tipo público/privado, documentos y cambio de estado |
| `MesaConsultaApi` | Audiencias | Oficialía | Mesas activas de una materia con sus abogados (Audiencias 3.3) |
| `RecepcionDemandaApi` | Audiencias | Oficialía | Enviar la demanda al abogado elegido (Audiencias 4.1; Oficialía 5.1) |
| `AudienciaConsultaApi` | Audiencias | Segunda | Audiencias, actas y resolución de primera instancia (Segunda 6.1) |
| `SolicitudPeritajeApi` | Peritajes | Audiencias | Pedir y cancelar peritajes y ver su estado (Peritajes 2.1–2.4; Audiencias 6.1, 6.4) |
| `DictamenConsultaApi` | Peritajes | Audiencias, Segunda | Leer dictámenes entregados (Peritajes 4.5; Audiencias 6.3; Segunda 6.1) |
| `SalaConsultaApi` | Segunda | Oficialía | Salas activas con su magistrado (Oficialía 5.2) |
| `RecepcionTocaApi` | Segunda | Oficialía | Crear la toca (Segunda 5.1; Oficialía 4.1, 5.2) |

Copias no ofrece contratos: solo usa los de Oficialía.

---

## 10. Seguridad

| Tema | Cómo se hace |
| :---- | :---- |
| Inicio de sesión | `formLogin` de Spring Security con sesión |
| Contraseñas | `BCryptPasswordEncoder`; nunca en texto plano ni en logs |
| Bloqueo | 3 intentos fallidos → 15 minutos (`intentos_fallidos`, `bloqueado_hasta`) |
| Recuperación | Token aleatorio guardado como hash, 15 minutos, un solo uso |
| CSRF | Activado. Thymeleaf agrega el token si el formulario usa `th:action` (también en el portal público) |
| Permisos | `@EnableMethodSecurity` + `@PreAuthorize("hasRole('...')")` en cada controller |
| Menú por rol | `sec:authorize` en las vistas |
| Datos propios | El service filtra por el usuario en sesión: el abogado ve sus expedientes, el perito sus casos, el magistrado sus tocas y Oficialía su adscripción. Si alguien pide algo ajeno, ve el mensaje del DFR o la página 404 |
| Usuario desactivado | No inicia sesión; el núcleo cierra sus sesiones abiertas |
| Portal público (Copias) | Solo `/copias/publico/**`. Sin datos de otros: con un folio se ve solo el estado del trámite. Los folios llevan una parte aleatoria para que no se puedan adivinar |
| Datos privados | La INE y el RFC de los solicitantes solo los ve `COPIAS_PERSONAL`. Los expedientes privados nunca se muestran en el portal |
| XSS | `th:text` siempre; `th:utext` solo para HTML limpiado con Jsoup |
| Inyección SQL | Spring Data JPA y `@Query` con parámetros |
| Archivos | Validación de tipo y tamaño, nombre aleatorio, fuera de `static/`, descarga solo con permiso |
| Bitácora | Sin edición ni borrado |
| Contraseñas del proyecto | En `application-local.properties`, que no se sube a GitHub |
| HTTPS | En el servidor de entrega. En local, `http://localhost:8080` |

---

## 11. Base de datos

- **Un solo esquema** `sij`.
- **JPA crea las tablas** a partir de las entidades (`spring.jpa.hibernate.ddl-auto=update`). No se escriben scripts.
- **Datos de prueba:** cada módulo tiene su clase `DatosIniciales<Modulo>` (`CommandLineRunner`) que inserta solo si sus tablas están vacías. No se usa `data.sql`, porque los cinco equipos lo editarían al mismo tiempo.
- **Orden de los datos de prueba:**
  1. Núcleo: usuarios con un rol de cada módulo.
  2. Oficialía: materias, distritos, juzgados y algunas demandas.
  3. Los demás módulos.

  Se ordena con `@Order` en cada clase.
- Si renombras o quitas un atributo de una entidad, avísalo en tu PR: JPA no borra columnas viejas.
- Antes de la entrega, los integradores exportan el esquema final a `docs/bd/esquema.sql`.

---

## 12. Árbol del repositorio

```text
sistema-judicial/
├── .github/
│   ├── CODEOWNERS
│   ├── pull_request_template.md
│   ├── copilot-instructions.md              # "Lee AGENTS.md"
│   ├── ISSUE_TEMPLATE/ (requerimiento.md, bug.md, contrato.md)
│   └── workflows/ci.yml
│
├── docs/
│   ├── ARQUITECTURA.md      CONTRATOS.md      FLUJO_DE_TRABAJO.md      GUIA_GITHUB.md
│   ├── bd/esquema.sql                         # al final
│   └── modulos/
│       ├── _PLANTILLA_MODULO.md
│       ├── oficialia.md   audiencias.md   peritajes.md   segunda-instancia.md   copias-certificadas.md
│       └── <prefijo>/                         # DFR y BPMN originales de cada equipo
│
├── src/main/java/mx/edu/utez/sij/
│   ├── SijApplication.java
│   ├── nucleo/                                # INTEGRADORES
│   │   ├── config/            # SecurityConfig, ZonaHorariaConfig, SesionPorRolHandler
│   │   ├── acceso/            # login, recuperación, cambio de contraseña, bloqueo
│   │   ├── usuarios/  notificaciones/  bitacora/  archivos/  pdf/
│   │   ├── web/               # InicioController, ErrorController, ControllerAdvice
│   │   └── contratos/         # ReglaNegocioException + una carpeta por módulo
│   │       └── oficialia/  audiencias/  peritajes/  segunda/
│   ├── oficialia/             # EQUIPO 1
│   ├── audiencias/            # EQUIPO 2
│   ├── peritajes/             # EQUIPO 3
│   ├── segunda/               # EQUIPO 4
│   └── copias/                # EQUIPO 5
│       # cada módulo: controller/ service/ repository/ model/ dto/ DatosIniciales<Modulo>.java
│
├── src/main/resources/
│   ├── application.properties                 # INTEGRADORES
│   ├── application-local.example.properties   # ejemplo (sí se sube)
│   ├── templates/
│   │   ├── nucleo/            # layout.html, layout-publico.html, login.html, recuperar.html, inicio.html
│   │   ├── error/             # 403.html, 404.html, 500.html
│   │   ├── correo/<prefijo>/  # plantillas de correo de cada módulo
│   │   ├── pdf/<prefijo>/     # plantillas que se convierten a PDF
│   │   └── oficialia/  audiencias/  peritajes/  segunda/  copias/
│   └── static/
│       ├── nucleo/ (css/, js/, img/)
│       └── oficialia/  audiencias/  peritajes/  segunda/  copias/
│
├── src/test/java/mx/edu/utez/sij/             # misma estructura
├── pom.xml   mvnw   mvnw.cmd   .mvn/          # INTEGRADORES
├── .gitignore   .gitattributes
├── AGENTS.md   CLAUDE.md   README.md
```

`application.properties` (solo lo editan los integradores):

```properties
spring.application.name=sij
spring.profiles.active=local
spring.datasource.url=jdbc:mysql://localhost:3306/sij?createDatabaseIfNotExist=true&serverTimezone=America/Mexico_City
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.servlet.multipart.max-file-size=25MB
spring.servlet.multipart.max-request-size=50MB
server.servlet.session.timeout=30m
sij.archivos.ruta=${user.home}/sij-archivos
sij.correo.habilitado=false
sij.acceso.intentos-maximos=3
sij.acceso.minutos-bloqueo=15
sij.acceso.minutos-token-recuperacion=15
sij.sesion.minutos-por-rol.SEGUNDA_MAGISTRADO=480
```

`application-local.properties` (cada quien el suyo; **no se sube**):

```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

**Correr el proyecto:** copia el ejemplo como `application-local.properties`, ejecuta `./mvnw spring-boot:run` y abre `http://localhost:8080`.

---

## 13. GitHub

- Cómo se configura el repositorio y cómo trabaja cada integrante: **`docs/GUIA_GITHUB.md`**.
- Las reglas obligatorias para personas y asistentes de IA: **`docs/FLUJO_DE_TRABAJO.md`**.

Resumen del modelo de ramas:

```text
main  ←  develop  ←  equipo-N  ←  equipo-N/<tarea>
(entrega)  (los 5 módulos)  (cada equipo)  (cada integrante)
```

---

## 14. Sprint 0 (integradores)

1. Proyecto de Spring Initializr con las dependencias de la sección 4 y los paquetes vacíos de los 5 módulos.
2. Núcleo funcionando:
   - login,
   - recuperación y cambio de contraseña,
   - bloqueo por intentos,
   - usuarios y roles,
   - notificaciones,
   - bitácora,
   - archivos,
   - PDF,
   - layout privado y público,
   - páginas de error.
3. Usuarios de prueba con un rol de cada módulo.
4. Todas las interfaces de `docs/CONTRATOS.md`, cada una con una clase de **datos de prueba** en el módulo dueño (`// TODO equipo N`).
5. Página `/` con tarjetas por módulo, y una página de inicio vacía en cada módulo.
6. `SecurityConfig` con `/copias/publico/**` como ruta pública.
7. `.gitignore`, ejemplo de `application-local`, CI, CODEOWNERS, plantilla de PR, `AGENTS.md` y `docs/`.
8. Cada equipo revisa su `docs/modulos/<modulo>.md` y su sección de "Correcciones sugeridas al DFR".

---

## 15. Decisiones pendientes entre equipos

Son puntos donde dos o más DFR no coinciden o les falta algo para conectarse. Cada uno tiene una sugerencia; lo deciden los equipos involucrados (con el cliente si hace falta) y después se actualizan sus DFR.

| # | Tema | Qué dice cada DFR | Sugerencia | Equipos |
| :---- | :---- | :---- | :---- | :---- |
| 1 | Comunicación entre módulos | Audiencias, Segunda y Copias piden "API REST con JSON" | Interfaces de Java dentro del mismo proyecto (sección 9). Corregir 3.1 y 3.3.6 de cada DFR | Todos |
| 2 | Catálogo de materias | Peritajes: Civil, Familiar, Mercantil, Penal. Segunda: civil, familiar, mercantil, "judicial". Oficialía: ejemplos Familiar, Laboral, Civil | Un solo catálogo que administra el Administrador Mayor (Oficialía 2.1). Datos iniciales: Civil, Familiar, Mercantil y Penal. Confirmar con el cliente si existe Laboral; "judicial" parece un error | 1, 2, 3, 4 |
| 3 | Mensaje al recuperar contraseña | Segunda: mensaje genérico. Audiencias y Copias: "el correo no está registrado" | Mensaje genérico, porque así nadie puede averiguar qué correos existen | 2, 4, 5 |
| 4 | ¿Mesa o abogado? | Oficialía 5.1: "asignar automáticamente una mesa" (y luego dice que el usuario la selecciona). Audiencias 3.2 y RN 3: Oficialía **elige al abogado** de una mesa de la materia | Oficialía elige mesa **y** abogado entre los que devuelve `MesaConsultaApi`, de forma manual. Corregir Oficialía 5.1 | 1, 2 |
| 5 | Tipo de demanda y partes | Audiencias 2.5 y Copias 2.2 necesitan saber si la demanda es **pública o privada** y quiénes son las partes (con RFC). Oficialía 3.1 no captura el tipo, el RFC del demandado ni los abogados, y deja opcionales los nombres | Agregar a Oficialía 3.1: Tipo (pública/privada)\*, Nombre del demandante\*, RFC del demandante\*, Nombre del demandado\*, RFC del demandado, Abogados de las partes | 1, 2, 5 |
| 6 | Número de expediente | Todos lo usan; nadie dice su formato ni cuándo se genera | Lo genera Oficialía al registrar la demanda, con formato `EXP-AAAA-NNNNN` [Propuesta]. Solo las procedentes llegan a Audiencias | 1 |
| 7 | Dictaminar procedencia | Oficialía lo pone en su alcance y en 2.2, pero no tiene RF en 3.2 | Agregar un RF "Dictaminar procedencia (Personal del juzgado)" con motivo obligatorio si es improcedente | 1 |
| 8 | ¿Siempre hay perito? | Peritajes 2.5: siempre hay perito disponible. Audiencias 6.1: "No hay peritos disponibles en esta materia" | Peritajes devuelve ese mensaje si no hay perito activo de la materia (no cuesta nada). Ajustar la suposición en el DFR de Peritajes | 2, 3 |
| 9 | Varios peritajes por expediente | Peritajes usa el folio de la demanda como único. Audiencias 6.4 habla de **varias** solicitudes por expediente | Peritajes genera su propio folio por solicitud y guarda el número de expediente (que se puede repetir) | 2, 3 |
| 10 | Cancelar una solicitud de peritaje | Peritajes 2.4: la cancela Audiencias el mismo día. Audiencias no tiene ese RF | Agregar a Audiencias un RF "Cancelar solicitud de peritaje" (el mismo día), o quitar 2.4 de Peritajes | 2, 3 |
| 11 | Estados del peritaje | Audiencias 6.4: pendiente / asignado / dictaminado. Peritajes: Pendiente, Asignado, En Proceso, Dictamen Entregado, Cancelado | `SolicitudPeritajeApi` traduce: Asignado y En Proceso → "asignado"; Dictamen Entregado → "dictaminado"; además "cancelado" | 2, 3 |
| 12 | Materia de las salas | Oficialía RN 2: la sala debe ser de la misma materia que la demanda. Segunda 3.1: las salas no tienen materia, solo distrito | Agregar Materia\* a la sala en Segunda 3.1, o quitar esa parte de la RN 2 de Oficialía | 1, 4 |
| 13 | Documento de apelación | Segunda 5.1 lo exige (PDF). Oficialía 4.1 solo pide el número de expediente | Agregar a Oficialía 4.1: Documento de apelación (PDF)\* | 1, 4 |
| 14 | Reasignar tocas | Segunda 4.5: hay que reasignar tocas antes de desactivar a un magistrado. Segunda RN 7: la asignación es exclusiva de Oficialía | Segunda permite reasignar la sala de una toca solo para ese caso, o Oficialía la reasigna. Lo decide el Equipo 4 | 1, 4 |
| 15 | Estado de la demanda | Oficialía usa "En Trámite", "Programada para Audiencia", "Resuelta" y "En Segunda Instancia", pero esos cambios ocurren en otros módulos | Audiencias avisa con `ExpedienteApi.cambiarEstado(...)`: En Trámite al recibirla, Resuelta al registrar la resolución. Confirmar si se necesita "Programada para Audiencia" y qué estado queda tras la resolución de segunda instancia | 1, 2, 4 |
| 16 | Avisos a las partes | Segunda 7.1: "se envía notificación por correo a las partes". Las partes no tienen cuenta ni correo capturado | Quitarlo de 7.1 (las notificaciones a las partes están pospuestas en Audiencias y en Segunda 2.6), o capturar el correo de las partes en Oficialía | 1, 4 |
| 17 | Tarifa de copias | Copias habla de "tarifa vigente" y de un panel de tarifas, pero no hay RF | Agregar un RF "Configurar tarifa" (`COPIAS_PERSONAL` o `SISTEMA_ADMIN`) o fijar un precio único inicial | 5 |
| 18 | ¿El personal de Copias es de un juzgado? | Copias no dice si el personal valida solicitudes de cualquier juzgado o solo del suyo | Confirmar con el cliente. Si es por juzgado, Copias guarda la adscripción como Oficialía | 5 |
