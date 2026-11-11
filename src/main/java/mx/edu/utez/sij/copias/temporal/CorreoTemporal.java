package mx.edu.utez.sij.copias.temporal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * TEMPORAL: en lugar de enviar el correo, lo imprime en la consola (como pide ARQUITECTURA 7.1 en desarrollo).
 * Cuando el núcleo tenga el envío de correos a destinatarios sin cuenta, se usa ese servicio y se borra esta clase.
 */
@Service
public class CorreoTemporal {

    private static final Logger log = LoggerFactory.getLogger(CorreoTemporal.class);

    /** Devuelve true si se "envió". El servicio real podrá devolver false si falla el SMTP. */
    public boolean enviar(String destinatario, String asunto, String cuerpo) {
        log.info("""

                ===== CORREO (simulado) =====
                Para:   {}
                Asunto: {}
                {}
                =============================""", destinatario, asunto, cuerpo);
        return true;
    }
}
