package mx.edu.utez.sij.copias.temporal;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TEMPORAL: expedientes de prueba mientras Oficialía no implementa ExpedienteApi.
 * Los números son de prueba porque Oficialía aún no define su formato (pendiente 6 de ARQUITECTURA).
 * Nombres y RFC ficticios.
 */
@Service
public class ExpedienteApiTemporal implements ExpedienteApi {

    private final Map<String, ExpedienteDto> expedientes = Map.of(
            "EXP-2026-001", new ExpedienteDto("EXP-2026-001", 1L, "Civil", TipoExpediente.PUBLICO,
                    1L, "Juzgado Primero Civil de Cuernavaca", LocalDateTime.of(2026, 9, 2, 10, 30),
                    List.of(new ParteDto("Laura Méndez Ortiz", "MEOL850312AB1", RolParte.DEMANDANTE),
                            new ParteDto("Constructora Río Azul S.A. de C.V.", "CRA100215KT3", RolParte.DEMANDADO))),
            "EXP-2026-002", new ExpedienteDto("EXP-2026-002", 2L, "Familiar", TipoExpediente.PRIVADO,
                    2L, "Juzgado Segundo Familiar de Jiutepec", LocalDateTime.of(2026, 9, 15, 9, 0),
                    List.of(new ParteDto("Andrea Castro Ruiz", "CARA900721MZ4", RolParte.DEMANDANTE),
                            new ParteDto("Miguel Ángel Soto Pérez", "SOPM880403H52", RolParte.DEMANDADO))),
            "EXP-2026-003", new ExpedienteDto("EXP-2026-003", 3L, "Mercantil", TipoExpediente.PUBLICO,
                    3L, "Juzgado Primero Mercantil de Cuernavaca", LocalDateTime.of(2026, 10, 1, 12, 15),
                    List.of(new ParteDto("Distribuidora Morelos S.A.", "DMO050630QX8", RolParte.DEMANDANTE),
                            new ParteDto("Jorge Luis Vega Ramos", "VERJ791110TT6", RolParte.DEMANDADO))),
            "EXP-2026-004", new ExpedienteDto("EXP-2026-004", 4L, "Penal", TipoExpediente.PRIVADO,
                    4L, "Juzgado Penal de Primera Instancia de Cuautla", LocalDateTime.of(2026, 10, 8, 16, 40),
                    List.of(new ParteDto("Rosa Elena Fuentes Lara", "FULR920215KD9", RolParte.DEMANDANTE),
                            new ParteDto("Héctor Ramírez Gil", "RAGH870930PL1", RolParte.DEMANDADO))));

    @Override
    public Optional<ExpedienteDto> buscar(String numeroExpediente) {
        if (numeroExpediente == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(expedientes.get(numeroExpediente.trim().toUpperCase()));
    }
}
