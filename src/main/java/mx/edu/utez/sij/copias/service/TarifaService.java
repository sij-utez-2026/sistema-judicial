package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.model.Tarifa;
import mx.edu.utez.sij.copias.repository.TarifaRepository;
import mx.edu.utez.sij.copias.temporal.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tarifa de las copias. El costo se congela en la solicitud al crearla.
 * Pendiente 17: quién configura la tarifa y si es por copia o por página. Por ahora hay una sola tarifa.
 */
@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    @Transactional(readOnly = true)
    public Tarifa vigente() {
        return tarifaRepository.findFirstByVigenteDesdeLessThanEqualOrderByVigenteDesdeDesc(Formatos.ahora().toLocalDate())
                .orElseThrow(() -> new ReglaNegocioException("No hay una tarifa vigente. Avise al administrador del área"));
    }

    @Transactional
    public Tarifa registrar(BigDecimal monto, LocalDate vigenteDesde, Long usuarioId) {
        if (monto == null || monto.signum() <= 0) {
            throw new ReglaNegocioException("La tarifa debe ser mayor a cero");
        }
        Tarifa tarifa = new Tarifa();
        tarifa.setMonto(monto);
        tarifa.setVigenteDesde(vigenteDesde);
        tarifa.setCreadaPor(usuarioId);
        return tarifaRepository.save(tarifa);
    }

    public long total() {
        return tarifaRepository.count();
    }
}
