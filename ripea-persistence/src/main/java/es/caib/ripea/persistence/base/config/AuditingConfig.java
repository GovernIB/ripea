package es.caib.ripea.persistence.base.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
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
	@Autowired private UsuariRepository usuariRepository;

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			if (SecurityContextHolder.getContext()!=null && SecurityContextHolder.getContext().getAuthentication()!=null) {
				//Si el usuari no existeix a la BBDD, perque es per exemple un usuari de integracio de portafib que esta modificant un document del qual
				//hem rebut la firma. Pot falla la FK cap a la taula de usuaris si es que existeix (en cas de IPA_CONT_LOG existeix)
				UsuariEntity  ue = usuariRepository.findByCodi(SecurityContextHolder.getContext().getAuthentication().getName());
				if (ue!=null) {
					return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
				} else {
					return Optional.empty();
				}
			} else {
				//TODO: Nomes per REACT, pendent de afegir autenticació a REACT
				return Optional.of("rip_admin");
			}
			
		};
	}
}