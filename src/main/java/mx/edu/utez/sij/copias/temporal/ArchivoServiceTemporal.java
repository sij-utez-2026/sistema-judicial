package mx.edu.utez.sij.copias.temporal;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * TEMPORAL: imita ArchivoService del núcleo (docs/ARQUITECTURA.md, 7.5) guardando los archivos en memoria
 * (se pierden al reiniciar). Cuando exista el núcleo se cambian los import y se borra esta clase.
 */
@Service
public class ArchivoServiceTemporal {

    /** Archivo guardado, como lo devolverá archivoService.descargar(id). */
    public record Archivo(Long id, String nombreOriginal, String tipo, byte[] contenido) {
    }

    private final Map<Long, Archivo> archivos = new LinkedHashMap<>();
    private long siguienteId = 1;

    /**
     * Valida extensión y tamaño, guarda el archivo y devuelve su id (en el núcleo: id de core_archivo).
     *
     * @param tiposPermitidos extensiones en minúsculas sin punto, por ejemplo ["pdf", "jpg", "png"]
     */
    public synchronized Long guardar(String modulo, MultipartFile archivo, List<String> tiposPermitidos,
                                     int tamanoMaximoMB) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ReglaNegocioException("Seleccione un archivo");
        }
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        int punto = nombre.lastIndexOf('.');
        String extension = punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
        if (extension.equals("jpeg")) {
            extension = "jpg";
        }
        if (!tiposPermitidos.contains(extension)) {
            throw new ReglaNegocioException("El archivo no es válido: solo se permiten "
                    + String.join(", ", tiposPermitidos).toUpperCase(Locale.ROOT));
        }
        if (archivo.getSize() > tamanoMaximoMB * 1024L * 1024L) {
            throw new ReglaNegocioException("El archivo no es válido: el tamaño máximo es de " + tamanoMaximoMB + " MB");
        }
        try {
            Archivo guardado = new Archivo(siguienteId++, nombre, archivo.getContentType(), archivo.getBytes());
            archivos.put(guardado.id(), guardado);
            return guardado.id();
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo guardar el archivo. Intente de nuevo");
        }
    }

    public synchronized Optional<Archivo> descargar(Long id) {
        return Optional.ofNullable(archivos.get(id));
    }
}
