# Módulo de <Nombre> — <descripción corta>

> Copia este archivo como `docs/modulos/<prefijo>.md` y llénalo con base en el DFR de tu equipo. Borra las instrucciones entre `< >`. Ejemplo completo: `docs/modulos/peritajes.md`.

| Dato | Valor |
| :---- | :---- |
| Equipo | Equipo <N> · rama `equipo-<N>` |
| Prefijo | Paquete `mx.edu.utez.sij.<prefijo>` · URLs `/<prefijo>/...` · vistas `templates/<prefijo>/` · tablas `<pre>_` · roles `<PREFIJO>_*` |
| Fuente | DFR v<x.y> y BPMN (`docs/modulos/<prefijo>/`) |

## 1. Propósito
<2 a 4 líneas: qué problema resuelve el módulo.>

## 2. Requerimientos y quién los implementa
| RF | Requerimiento | Implementación (módulo / núcleo / contrato) |
| :---- | :---- | :---- |
| | | |

> El login, el cambio de contraseña, los usuarios, las notificaciones, la bitácora y los archivos los da el **núcleo**. No los programes en tu módulo.

## 3. Roles
| Rol | Puede |
| :---- | :---- |
| `<PREFIJO>_ADMIN` | |

## 4. Modelo de datos
<Diagrama Mermaid `erDiagram` solo con tus tablas `<pre>_*`. Puedes relacionarte con `core_usuario` y `core_archivo`; con otros módulos (incluidas materias y juzgados de Oficialía) guarda solo su id o folio, sin relación de tabla.>

## 5. Estados
<Diagrama `stateDiagram-v2` de tus entidades principales; los estados serán un `enum` de Java.>

## 6. Reglas de negocio en el código
<Cada regla del DFR explicada como validación o paso dentro de un service.>

## 7. Contratos
### 7.1 Que ofrece este módulo (`nucleo/contratos/<prefijo>/`)
<Interfaz Java con sus métodos y los records de datos.>

### 7.2 Que necesita de otros módulos
| De | Qué | Para qué |
| :---- | :---- | :---- |

> Si necesitas algo de otro módulo que no existe, abre un issue con la etiqueta `contrato`.

## 8. Rutas (`/<prefijo>`)
| Método | URL | Roles | Vista / acción |
| :---- | :---- | :---- | :---- |

> Para ver se usa `GET`; para guardar, cambiar o borrar, siempre `POST` con formulario.

## 9. Estructura de carpetas del módulo
<Clases por paquete (controller, service, repository, model, dto) y vistas en `templates/<prefijo>/`.>

## 10. Pendientes
