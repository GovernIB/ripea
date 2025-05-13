package es.caib.ripea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal del backoffice de RIPEA per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@SpringBootApplication
@EnableAsync
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class RipeaBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(RipeaBackBootApp.class, args);
	}

}
