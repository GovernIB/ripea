package es.caib.ripea.persistence.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

/**
 * Aplicació Spring Boot mínima per a l'execució dels tests d'integració
 * del mòdul de persistència. Escaneja el paquet de persistència i exclou
 * les autoconfiguracions que no es necessiten en els tests.
 */
@SpringBootApplication(
        scanBasePackages = "es.caib.ripea.persistence",
        exclude = {
                LiquibaseAutoConfiguration.class,
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
public class RipeaTestApplication {
}
