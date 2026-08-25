package es.caib.ripea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal del backoffice de RIPEA per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@SpringBootApplication
@EnableAsync
// Necessari per al ping de manteniment de les connexions SSE (SseResourceController): el context
// de ripea-back exclou es.caib.ripea.service.*, on hi ha l'únic @EnableScheduling de l'aplicació.
@EnableScheduling
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class RipeaBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(RipeaBackBootApp.class, args);
	}

}
