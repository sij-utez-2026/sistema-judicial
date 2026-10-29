# Contratos entre módulos

> Ubicación del código: `src/main/java/mx/edu/utez/sij/nucleo/contratos/<modulo>/`
>
> Un contrato es una **interfaz de Java** con sus datos (`record`). El módulo dueño escribe la clase que la implementa; los demás solo la usan. **Las firmas de este documento no se cambian** sin un issue con la etiqueta `contrato` aprobado por los integradores.
>
> Los campos marcados **[Confirmar]** dependen de la sección 15 de `docs/ARQUITECTURA.md`.

---

## 1. Cómo se usa un contrato

```java
// 1. La interfaz vive en el núcleo (la crean los integradores en el Sprint 0)
package mx.edu.utez.sij.nucleo.contratos.audiencias;

public interface MesaConsultaApi {
    List<MesaDisponibleDto> mesasDisponibles(Long materiaId);
}

// 2. El módulo dueño la implementa (Equipo 2)
package mx.edu.utez.sij.audiencias.service;

@Service
@RequiredArgsConstructor
public class MesaConsultaService implements MesaConsultaApi {
    private final MesaRepository mesaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MesaDisponibleDto> mesasDisponibles(Long materiaId) {
        // Solo mesas activas de esa materia con abogados activos (Audiencias RF 3.3)
        ...
    }
}

// 3. Otro módulo la usa (Equipo 1); Spring inyecta la de Audiencias
package mx.edu.utez.sij.oficialia.service;

@Service
@RequiredArgsConstructor
public class EnvioDemandaService {
    private final MesaConsultaApi mesaConsultaApi;   // nunca MesaRepository

    public List<MesaDisponibleDto> opciones(Long materiaId) {
        return mesaConsultaApi.mesasDisponibles(materiaId);
    }
}
```

**Mientras el dueño no termina:** en el Sprint 0 los integradores crean la clase del dueño con datos de prueba fijos y un comentario `// TODO equipo N: implementar`. El equipo dueño cambia solo el contenido de los métodos.

**Errores:** todos los errores de negocio extienden `ReglaNegocioException`. Quien llama atrapa esa excepción y muestra el mensaje:

```java
// nucleo/contratos/ReglaNegocioException.java
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) { super(mensaje); }
}

// Uso en un controller
try {
    solicitudPeritajeApi.solicitar(datos);
    flash.addFlashAttribute("exito", "Solicitud registrada");
} catch (ReglaNegocioException e) {
    flash.addFlashAttribute("error", e.getMessage());   // p. ej. "No hay peritos disponibles en esta materia"
}
```

---

## 2. Mapa de contratos

| Contrato | Implementa | Usan |
| :---- | :---- | :---- |
| `CatalogoJudicialApi` | Equipo 1 · Oficialía | Todos |
| `ExpedienteApi` | Equipo 1 · Oficialía | 2, 3, 4, 5 |
| `MesaConsultaApi` | Equipo 2 · Audiencias | 1 |
| `RecepcionDemandaApi` | Equipo 2 · Audiencias | 1 |
| `AudienciaConsultaApi` | Equipo 2 · Audiencias | 4 |
| `SolicitudPeritajeApi` | Equipo 3 · Peritajes | 2 |
| `DictamenConsultaApi` | Equipo 3 · Peritajes | 2, 4 |
| `SalaConsultaApi` | Equipo 4 · Segunda Instancia | 1 |
| `RecepcionTocaApi` | Equipo 4 · Segunda Instancia | 1 |

---

## 3. Oficialía de Partes (`contratos/oficialia/`)

### 3.1 `CatalogoJudicialApi`

Fuente: Oficialía RF 2.1 y 2.2. Los demás módulos nunca guardan copias de estos catálogos: solo guardan el `id`.

```java
public interface CatalogoJudicialApi {
    List<MateriaDto> materiasActivas();
    Optional<MateriaDto> materia(Long materiaId);
    List<DistritoDto> distritosActivos();                // Segunda RF 2.1
    List<JuzgadoDto> juzgados(Long distritoId);          // distritoId null = todos
    Optional<JuzgadoDto> juzgado(Long juzgadoId);
}

public record MateriaDto(Long id, String nombre) {}
public record DistritoDto(Long id, String nombre, String descripcion) {}
public record JuzgadoDto(Long id, String nombre, Long materiaId,
                         Long municipioId, String municipio, Long distritoId) {}
```

### 3.2 `ExpedienteApi`

Fuente: Oficialía RF 3.1. Lo usan Audiencias 4.1–4.3, Peritajes 4.2, Segunda 6.1 y Copias 2.1–3.1.

```java
public interface ExpedienteApi {
    Optional<ExpedienteDto> buscar(String numeroExpediente);
    void cambiarEstado(String numeroExpediente, EstadoExpediente nuevoEstado, String moduloOrigen);
}

public record ExpedienteDto(
        String numeroExpediente,
        Long materiaId,
        String materia,
        TipoExpediente tipo,               // [Confirmar] Oficialía debe capturarlo (pendiente 5)
        Long juzgadoId,
        String juzgado,
        EstadoExpediente estado,
        LocalDateTime fechaRegistro,
        List<ParteDto> partes,
        List<Long> documentosIds) {}       // ids de core_archivo

public record ParteDto(String nombre, String rfc, RolParte rol) {}

public enum TipoExpediente { PUBLICO, PRIVADO }
public enum RolParte { DEMANDANTE, DEMANDADO, ABOGADO }   // ABOGADO [Confirmar] (pendiente 5)

public enum EstadoExpediente {                            // Oficialía RF 3.1, 4.1, 5.1 [Confirmar] (pendiente 15)
    PENDIENTE_REVISION, PROCEDENTE, IMPROCEDENTE,
    EN_TRAMITE, PROGRAMADA_AUDIENCIA, RESUELTA, EN_SEGUNDA_INSTANCIA
}
```

| Quién llama a `cambiarEstado` | Cuándo | Nuevo estado |
| :---- | :---- | :---- |
| Audiencias | Al recibir la demanda (RF 4.1) | `EN_TRAMITE` |
| Audiencias | Al programar la primera audiencia (RF 5.1) [Confirmar] | `PROGRAMADA_AUDIENCIA` |
| Audiencias | Al registrar la resolución (RF 5.9) | `RESUELTA` |

Oficialía decide qué cambios acepta y lanza `ReglaNegocioException` si no proceden.

**Seguridad:** `buscar` devuelve los datos completos. Cada módulo decide qué mostrar según sus reglas. Por ejemplo, Copias **nunca** muestra las partes en el portal público; solo las usa para que el personal valide.

---

## 4. Audiencias (`contratos/audiencias/`)

### 4.1 `MesaConsultaApi`

Fuente: Audiencias RF 3.3. Lo usa Oficialía RF 5.1.

```java
public interface MesaConsultaApi {
    // Solo mesas activas de la materia, con sus abogados activos. Lista vacía si no hay.
    List<MesaDisponibleDto> mesasDisponibles(Long materiaId);
}

public record MesaDisponibleDto(Long mesaId, String nombre, Long materiaId, List<AbogadoDto> abogados) {}
public record AbogadoDto(Long usuarioId, String nombreCompleto, String numeroEmpleado) {}
```

### 4.2 `RecepcionDemandaApi`

Fuente: Audiencias RF 4.1. Lo usa Oficialía RF 5.1.

```java
public interface RecepcionDemandaApi {
    // Crea el expediente en el listado del abogado con estado "Pendiente de audiencia".
    void recibir(DemandaAsignadaDto demanda);
}

public record DemandaAsignadaDto(String numeroExpediente, Long materiaId, Long mesaId, Long abogadoUsuarioId) {}
```

- Las partes, el tipo y las pruebas **no viajan aquí**: Audiencias los lee con `ExpedienteApi.buscar`, así no hay datos duplicados.
- Errores (`ReglaNegocioException`):
  - "El expediente no existe o no es procedente".
  - "El abogado no pertenece a una mesa activa de esa materia".
  - "El expediente ya fue enviado a Audiencias".

### 4.3 `AudienciaConsultaApi`

Fuente: Segunda RF 6.1 (el magistrado ve audiencias, actas y resolución de primera instancia).

```java
public interface AudienciaConsultaApi {
    List<AudienciaResumenDto> audienciasDeExpediente(String numeroExpediente);
    Optional<ResolucionPrimeraDto> resolucion(String numeroExpediente);
}

public record AudienciaResumenDto(Long audienciaId, LocalDateTime fechaHora, String tipo, String estado,
                                  String resultado, String acuerdos, Long actaFirmadaArchivoId) {}
public record ResolucionPrimeraDto(String numeroExpediente, String sentido, String resolucion, LocalDateTime fecha) {}
```

---

## 5. Peritajes (`contratos/peritajes/`)

### 5.1 `SolicitudPeritajeApi`

Fuente: Peritajes RF 2.1–2.4. Lo usa Audiencias RF 6.1 y 6.4.

```java
public interface SolicitudPeritajeApi {
    ResultadoSolicitudDto solicitar(SolicitudPeritajeDto solicitud);   // asigna perito automáticamente
    void cancelar(String folioPeritaje);                                 // solo el mismo día [Confirmar] (pendiente 10)
    List<EstadoPeritajeDto> solicitudesDeExpediente(String numeroExpediente);
}

public record SolicitudPeritajeDto(String numeroExpediente, Long audienciaId, Set<Long> materiasIds, String observaciones) {}
public record ResultadoSolicitudDto(String folioPeritaje, String estado, String peritoNombre) {}
public record EstadoPeritajeDto(String folioPeritaje, Long audienciaId, String materias, String peritoNombre,
                                LocalDateTime fechaSolicitud, String estado) {}
```

- `estado` usa los textos de Audiencias 6.4: `pendiente`, `asignado`, `dictaminado` y `cancelado` (pendiente 11).
- Errores:
  - "No hay peritos disponibles en esta materia" (Audiencias 6.1).
  - "Ya existe una solicitud activa para esta audiencia y materia".
  - "El peritaje solo puede cancelarse el mismo día de su solicitud".

### 5.2 `DictamenConsultaApi`

Fuente: Peritajes RF 4.5. Lo usan Audiencias RF 6.3 y Segunda RF 6.1.

```java
public interface DictamenConsultaApi {
    Optional<DictamenDto> dictamen(String folioPeritaje);              // vacío = "Dictamen pendiente"
    List<DictamenDto> dictamenesDeExpediente(String numeroExpediente);   // solo los entregados
}

public record DictamenDto(String folioPeritaje, String numeroExpediente, String peritoNombre,
                          LocalDateTime fechaEntrega, String contenidoHtml, List<Long> archivosIds) {}
```

`contenidoHtml` ya viene limpio (Jsoup). Se puede mostrar con `th:utext`.

---

## 6. Segunda Instancia (`contratos/segunda/`)

### 6.1 `SalaConsultaApi`

Fuente: Segunda RF 3.2 y 4.3. Lo usa Oficialía RF 5.2.

```java
public interface SalaConsultaApi {
    // Salas activas del distrito con magistrado activo asignado.
    List<SalaDisponibleDto> salasDisponibles(Long distritoId);
}

public record SalaDisponibleDto(Long salaId, String nombre, Long distritoId, String direccion,
                                Long magistradoUsuarioId, String magistradoNombre) {}
                                // [Confirmar] agregar materiaId (pendiente 12)
```

### 6.2 `RecepcionTocaApi`

Fuente: Segunda RF 5.1. Lo usa Oficialía RF 4.1 y 5.2.

```java
public interface RecepcionTocaApi {
    // Crea la toca y devuelve su folio. El magistrado es el de la sala elegida.
    String recibir(SolicitudTocaDto solicitud);
}

public record SolicitudTocaDto(String numeroExpediente, Long materiaId, Long juzgadoOrigenId,
                               Long distritoId, Long salaId, Long documentoApelacionArchivoId) {}
                               // documentoApelacionArchivoId [Confirmar] (pendiente 13)
```

- Errores:
  - "Faltan datos obligatorios para generar la toca".
  - "La sala no está activa".
  - "El expediente ya tiene una toca abierta".
- Segunda 5.1 dice "si faltan datos, la toca no se genera y se registra un error en el log": además del mensaje, Segunda lo registra en la bitácora.

---

## 7. Proceso para cambiar un contrato

1. Abrir un issue con la etiqueta `contrato`: qué método o campo, por qué y qué RF lo pide.
2. Lo comentan el equipo dueño y los equipos que lo usan.
3. Los integradores aprueban y cambian la interfaz en `nucleo/contratos/` (con un PR propio).
4. El dueño actualiza su implementación y los usuarios ajustan sus llamadas.
5. Se actualiza este documento en el mismo PR del paso 3.
