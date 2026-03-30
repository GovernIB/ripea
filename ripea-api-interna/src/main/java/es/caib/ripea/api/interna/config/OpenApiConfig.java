package es.caib.ripea.api.interna.config;

import java.io.IOException;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration("apiInternaOpenApiConfig")
@SecurityScheme(
	    name = "basicAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "basic"
	)
public class OpenApiConfig {

	/*private final SwaggerUiConfigProperties swaggerUiConfigProperties;
	
	public OpenApiConfig(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
    }
	
	@Bean
    public InitializingBean swaggerUiCustomizer() {
		//Con with-credentials: false, el navegador no adjuntará la cookie JSESSIONID a los requests de Swagger UI, 
		//así Spring Security solo verá la cabecera Authorization: Basic y pasará por tu AuthenticationDetailsSource correctamente.
        return () -> swaggerUiConfigProperties.setWithCredentials(false);
    }*/
	
	@Bean
	public OpenAPI customOpenAPI() {
		String version = "Unknown";
		try {
			Manifest manifest = new Manifest(getClass().getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			version = attributes.getValue("Implementation-Version");
		} catch (IOException ex) {
			log.error("No s'ha pogut obtenir la versió del fitxer MANIFEST.MF", ex);
		}
		OpenAPI openapi = new OpenAPI().info(
				new Info().
				title("API interna de RIPEA").
				description("API REST per integració amb aplicacions de la CAIB i consulta de dades obertes.").
				contact(new Contact().email("ripea.suport@limit.es")).
				version(version));
			//.addSecurityItem(new SecurityRequirement().addList("basicAuth"));
		return openapi;
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonTimeZoneCustomizer() {
	    return builder -> builder.timeZone(TimeZone.getTimeZone("Europe/Madrid"));
	}
}