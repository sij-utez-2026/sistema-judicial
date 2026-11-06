package mx.edu.utez.sij.copias.service;

import mx.edu.utez.sij.copias.model.Formatos;
import mx.edu.utez.sij.copias.repository.PagoRepository;
import mx.edu.utez.sij.copias.repository.SolicitudCopiaRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.function.Predicate;

/**
 * Folios del módulo con una parte aleatoria para que no se puedan adivinar (copias-certificadas.md 6.3):
 * - solicitud: CC-AAAA-XXXXXX
 * - comprobante de pago: CP-AAAA-XXXXXX
 * Sin letras que se confunden (I, O, 0, 1).
 */
@Service
public class FolioService {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LARGO = 6;

    private final SecureRandom aleatorio = new SecureRandom();
    private final SolicitudCopiaRepository solicitudRepository;
    private final PagoRepository pagoRepository;

    public FolioService(SolicitudCopiaRepository solicitudRepository, PagoRepository pagoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.pagoRepository = pagoRepository;
    }

    public String folioSolicitud() {
        return generar("CC", solicitudRepository::existsByFolio);
    }

    public String folioPago() {
        return generar("CP", pagoRepository::existsByFolioPago);
    }

    // Repite si por casualidad ya existe
    private String generar(String prefijo, Predicate<String> yaExiste) {
        String folio;
        do {
            StringBuilder parte = new StringBuilder();
            for (int i = 0; i < LARGO; i++) {
                parte.append(CARACTERES.charAt(aleatorio.nextInt(CARACTERES.length())));
            }
            folio = prefijo + "-" + Formatos.ahora().getYear() + "-" + parte;
        } while (yaExiste.test(folio));
        return folio;
    }
}
