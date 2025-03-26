package es.caib.ripea.persistence.base.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.service.intf.config.PropertyConfig;

/**
 * Configuració per a les entitats de base de dades auditables.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {

	@Value("${" + PropertyConfig.DEFAULT_AUDITOR + ":unknown}")
	private String defaultAuditor;

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			if (SecurityContextHolder.getContext()!=null && SecurityContextHolder.getContext().getAuthentication()!=null) {
				return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
			} else {
				return Optional.of("rip_admin");
			}
		};
	}
}