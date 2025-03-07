package es.caib.ripea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe principal del backoffice de RIPEA per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@SpringBootApplication
@ConditionalOnNotWarDeployment
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class RipeaApiInternaBootApp {

	public static void main(String[] args) {
		SpringApplication.run(RipeaApiInternaBootApp.class, args);
	}

}
