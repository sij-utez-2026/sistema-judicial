# Diccionario de datos — Copias Certificadas (Equipo 5)

Versión 1.0 · 06/11/2026 · Basado en las entidades de `mx.edu.utez.sij.copias.model`.

**Convenciones**
- Tablas con prefijo `cop_`. Motor MySQL 8, esquema `sij`. JPA crea las tablas (`ddl-auto=update`).
- **FK hacia el núcleo** (`core_*`): sí. **Hacia Oficialía**: no; solo se guarda el id o el número de expediente.
- Columnas `*_por` y `usuario_id`: id de `core_usuario` (FK cuando exista el núcleo).
- Fechas en `America/Mexico_City`. Dinero en `DECIMAL(10,2)`.
- Enums guardados como texto (`@Enumerated(EnumType.STRING)`).
- **[Propuesta]**: idea del equipo que no viene en el DFR.

Resumen:

| Tabla | Entidad | Para qué |
| :---- | :---- | :---- |
| `cop_solicitud` | `SolicitudCopia` | Solicitud de copia certificada |
| `cop_pago` | `Pago` | Pago simulado y folio del comprobante |
| `cop_entrega` | `Entrega` | Entrega física de la copia |
| `cop_correo` | `CorreoEnviado` | Correos enviados al solicitante [Propuesta] |
| `cop_solicitud_historial` | `HistorialSolicitud` | Cambios de estado |
| `cop_tarifa` | `Tarifa` | Tarifa vigente |
| `cop_color` | `ColorArea` | Colores de las pantallas de Copias |

---

## `cop_solicitud` — solicitud de copia certificada

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | Identificador |
| folio | VARCHAR(20) | No | UK | `CC-AAAA-XXXXXX`, con 6 caracteres aleatorios |
| numero_expediente | VARCHAR(30) | No | | Expediente de Oficialía (sin FK) |
| tipo | VARCHAR(10) | No | | `PUBLICO` / `PRIVADO`, tomado de `ExpedienteApi` al capturar |
| juzgado_id | BIGINT | No | | Juzgado (Oficialía, sin FK) |
| juzgado | VARCHAR(150) | No | | Nombre del juzgado al capturar, para el registro y el comprobante |
| materia_id | BIGINT | No | | Materia (Oficialía, sin FK) |
| materia | VARCHAR(80) | No | | Nombre de la materia al capturar |
| solicitante_nombre | VARCHAR(150) | No | | Nombre completo |
| solicitante_rfc | VARCHAR(13) | No | | RFC: 12 caracteres (moral) o 13 (física), en mayúsculas |
| solicitante_correo | VARCHAR(150) | No | | Para el comprobante y el aviso, en minúsculas |
| ine_archivo_id | BIGINT | Sí | FK `core_archivo` | Solo privadas: PDF, JPG o PNG de hasta 5 MB |
| estado | VARCHAR(30) | No | | Ver `EstadoSolicitud` |
| tarifa_id | BIGINT | No | FK `cop_tarifa` | Tarifa aplicada |
| costo | DECIMAL(10,2) | No | | Costo congelado al crear la solicitud |
| motivo_rechazo | VARCHAR(500) | Sí | | Obligatorio al rechazar [Propuesta] |
| capturado_por | BIGINT | No | FK `core_usuario` | Trabajador que capturó |
| validado_por | BIGINT | Sí | FK `core_usuario` | Quién aprobó o rechazó (solo privadas) |
| fecha_solicitud | DATETIME | No | | Fecha de captura |
| fecha_validacion | DATETIME | Sí | | Fecha del visto bueno o rechazo |
| actualizado_en | DATETIME | Sí | | Último cambio de estado |

Del expediente solo se guardan los datos esenciales. Las **partes** y los **documentos** no se guardan: se consultan con `ExpedienteApi` al validar.

## `cop_pago` — pago simulado

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| solicitud_id | BIGINT | No | FK `cop_solicitud`, UK | Una solicitud se paga una sola vez |
| folio_pago | VARCHAR(20) | No | UK | `CP-AAAA-XXXXXX`, folio del comprobante |
| monto | DECIMAL(10,2) | No | | Igual al `costo` de la solicitud |
| simulado | BOOLEAN | No | | Siempre `true`: no hay cargo real |
| registrado_por | BIGINT | No | FK `core_usuario` | |
| fecha | DATETIME | No | | |

## `cop_entrega` — entrega física

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| solicitud_id | BIGINT | No | FK `cop_solicitud`, UK | Una entrega por solicitud |
| entregado_por | BIGINT | No | FK `core_usuario` | |
| fecha | DATETIME | No | | Después de entregar, el folio queda inhabilitado (RN-03) |

## `cop_correo` — correos enviados [Propuesta]

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| solicitud_id | BIGINT | No | FK `cop_solicitud` | |
| tipo | VARCHAR(20) | No | | `COMPROBANTE` / `LISTA_PARA_RECOGER` |
| destinatario | VARCHAR(150) | No | | Correo al que se envió |
| enviado_por | BIGINT | No | FK `core_usuario` | |
| enviado_en | DATETIME | No | | |
| exito | BOOLEAN | No | | `false` si falló el envío, para reintentar |

## `cop_solicitud_historial` — cambios de estado

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| solicitud_id | BIGINT | No | FK `cop_solicitud` | |
| estado_anterior | VARCHAR(30) | Sí | | Nulo al crear la solicitud |
| estado_nuevo | VARCHAR(30) | No | | |
| usuario_id | BIGINT | No | FK `core_usuario` | Quién hizo el cambio |
| comentario | VARCHAR(500) | Sí | | Por ejemplo, el motivo del rechazo o el folio del pago |
| fecha | DATETIME | No | | |

## `cop_tarifa` — tarifas

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| monto | DECIMAL(10,2) | No | | Al inicio, $320.00 |
| vigente_desde | DATE | No | | La vigente es la más reciente que ya empezó |
| creada_por | BIGINT | Sí | FK `core_usuario` | Quién la configura: [Confirmar, pendiente 17] |

## `cop_color` — colores del área

| Columna | Tipo | Nulo | Clave | Descripción |
| :-- | :-- | :-- | :-- | :-- |
| id | BIGINT AI | No | PK | |
| variable | VARCHAR(40) | No | UK | Variable CSS (ver abajo) |
| valor | VARCHAR(7) | No | | `#RRGGBB` (se valida en el service) |
| actualizado_por | BIGINT | Sí | FK `core_usuario` | `COPIAS_ADMIN` |
| actualizado_en | DATETIME | Sí | | |

| variable | Uso | Inicial |
| :-- | :-- | :-- |
| `--sij-primario` | Menú lateral (Bosque) | `#35483B` |
| `--cop-titulos` | Títulos (Tierra) | `#76513D` |
| `--cop-boton` | Botones principales (Bugambilia) | `#651747` |
| `--sij-acento` | Detalles (Dorado) | `#B49A5A` |
| `--sij-borde` | Bordes (Piedra) | `#B8B0A1` |
| `--cop-aviso` | Avisos (Caña) | `#D8C39A` |

---

## Enum `EstadoSolicitud`

| Valor | Texto en pantalla | Llega desde |
| :-- | :-- | :-- |
| `EN_ESPERA_VALIDACION` | En espera de validación | Captura de una privada |
| `APROBADO` | Aprobado | Visto bueno |
| `LISTO_PARA_PAGO` | Listo para pago | Captura de una pública |
| `CANCELADO` | Cancelado | Rechazo en la validación |
| `PAGADO_EN_PREPARACION` | Pagado y en preparación | Pago |
| `LISTO_PARA_RECOGER` | Lista para recoger [Propuesta] | Aviso por correo |
| `ENTREGADO` | Entregado | Entrega |

## Enum `TipoCorreo`

| Valor | Texto |
| :-- | :-- |
| `COMPROBANTE` | Comprobante de pago |
| `LISTA_PARA_RECOGER` | Aviso: copia lista para recoger |

---

## Tablas del núcleo que usa Copias (no son nuestras)

| Tabla | Para qué |
| :-- | :-- |
| `core_usuario`, `core_usuario_rol` | Trabajadores (`COPIAS_PERSONAL`) y administradores (`COPIAS_ADMIN`) |
| `core_archivo` | La INE |
| `core_bitacora` | Registro de cada acción |

**Pendiente 18:** si se decide que cada trabajador atiende solo su juzgado, se agrega `cop_adscripcion (usuario_id FK core_usuario, juzgado_id)`.
