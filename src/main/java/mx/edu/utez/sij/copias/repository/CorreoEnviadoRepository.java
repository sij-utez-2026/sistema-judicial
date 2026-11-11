package mx.edu.utez.sij.copias.repository;

import mx.edu.utez.sij.copias.model.CorreoEnviado;

import java.util.List;

/** Correos enviados. Para JPA: extends JpaRepository<CorreoEnviado, Long> y borrar save (ver SolicitudCopiaRepository). */
public interface CorreoEnviadoRepository {

    CorreoEnviado save(CorreoEnviado correo);

    /** Los más recientes primero. */
    List<CorreoEnviado> findBySolicitudIdOrderByEnviadoEnDesc(Long solicitudId);
}
