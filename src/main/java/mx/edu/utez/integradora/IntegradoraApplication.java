package mx.edu.utez.integradora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Escanea tambien mx.edu.utez.sij, que es el paquete de los modulos segun docs/ARQUITECTURA.md
@SpringBootApplication(scanBasePackages = {"mx.edu.utez.integradora", "mx.edu.utez.sij"})
public class IntegradoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegradoraApplication.class, args);
	}

}
