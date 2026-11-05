package mx.edu.utez.sij.nucleo.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.ConfigurableServletWebServerFactory;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de las pantallas JSP (docs/ARQUITECTURA.md, sección 16.7).
 * Solo la modifican los integradores.
 */
@Configuration
public class FrontEndConfig {

    /**
     * Al correr con IntelliJ o con ./mvnw spring-boot:run, las JSP y los recursos se leen de la carpeta
     * front-end/ de la raíz del repositorio. En el .war empaquetado ya vienen dentro y no hace falta.
     * Por eso el proyecto se corre siempre desde la carpeta raíz.
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> carpetaFrontEnd() {
        return factory -> {
            File carpeta = new File("front-end");
            if (carpeta.isDirectory()) {
                factory.setDocumentRoot(carpeta);
            }
        };
    }

    /**
     * Páginas de error: front-end/WEB-INF/jsp/error/403.jsp, 404.jsp y 500.jsp.
     * Spring Boot no las encuentra solo porque están fuera de src/main/webapp.
     */
    @Bean
    public ErrorViewResolver vistasDeError() {
        return (request, status, modelo) -> {
            String vista = switch (status.value()) {
                case 403 -> "error/403";
                case 404 -> "error/404";
                default -> "error/500";
            };
            // Spring Boot manda "error" = "Not Found"; se quita para que <n:mensajes/> no lo muestre como alerta
            Map<String, Object> datos = new HashMap<>(modelo);
            datos.remove("error");
            return new ModelAndView(vista, datos, status);
        };
    }
}
