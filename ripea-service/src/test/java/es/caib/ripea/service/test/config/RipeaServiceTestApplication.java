package es.caib.ripea.service.test.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Aplicació Spring Boot mínima per als tests d'integració del mòdul de serveis.
 *
 * Escaneja tot "es.caib.ripea.service" però exclou el subpaquet "config"
 * per evitar que es carreguin les configuracions problemàtiques per als tests
 * (AclConfig, JmsConfig, SchedulingConfig). Les altres auto-configuracions
 * web i JMS es desactiven via "exclude".
 */
@SpringBootApplication(
        scanBasePackages = {
                "es.caib.ripea.persistence",
                "es.caib.ripea.service.test"
        },
        exclude = {
                LiquibaseAutoConfiguration.class,
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                JmsAutoConfiguration.class,
                ArtemisAutoConfiguration.class,
                WebMvcAutoConfiguration.class
        },
        excludeName = {
                // spring-filter registra SpringFilterWebConfig que implementa WebMvcConfigurer
                // (de spring-webmvc, no disponible al classpath de ripea-service)
                "com.turkraft.springfilter.boot.SpringFilterWebConfig"
        }
)
@ComponentScan(
        basePackages = {
                "es.caib.ripea.persistence",
                "es.caib.ripea.service",
                "es.caib.ripea.service.test"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "es\\.caib\\.ripea\\.service\\.config\\..*"
        )
)
public class RipeaServiceTestApplication {
}
