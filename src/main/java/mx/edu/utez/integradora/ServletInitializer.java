package mx.edu.utez.integradora;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Necesaria porque el proyecto se empaqueta como War (lo exige JSP).
 * Permite desplegar el .war en un Tomcat externo. No se modifica.
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(IntegradoraApplication.class);
	}

}
