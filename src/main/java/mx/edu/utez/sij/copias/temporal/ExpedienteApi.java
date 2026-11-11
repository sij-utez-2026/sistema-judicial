package mx.edu.utez.sij.copias.temporal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TEMPORAL: copia del contrato ExpedienteApi de docs/CONTRATOS.md (3.2), solo con lo que usa Copias.
 * Cuando los integradores creen nucleo/contratos/oficialia/ExpedienteApi:
 * 1. se cambian los import a los del contrato,
 * 2. se borran esta interfaz y ExpedienteApiTemporal (Spring inyectará la implementación de Oficialía).
 */
public interface ExpedienteApi {

    Optional<ExpedienteDto> buscar(String numeroExpediente);

    enum TipoExpediente { PUBLICO, PRIVADO }

    enum RolParte { DEMANDANTE, DEMANDADO, ABOGADO }

    record ParteDto(String nombre, String rfc, RolParte rol) {
    }

    record ExpedienteDto(String numeroExpediente,
                         Long materiaId,
                         String materia,
                         TipoExpediente tipo,
                         Long juzgadoId,
                         String juzgado,
                         LocalDateTime fechaRegistro,
                         List<ParteDto> partes) {
    }
}
