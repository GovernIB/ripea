/**
 * 
 */
package es.caib.ripea.core.audit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.core.persistence.UsuariEntity;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RipeaAuditorAware implements AuditorAware<UsuariEntity> {

	@Resource
	private UsuariRepository usuariRepository;

	@Override
	public UsuariEntity getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String auditorActual = (auth != null) ? auth.getName() : null;
		LOGGER.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		if (auditorActual == null) {
			LOGGER.debug("Auditor actual: null");
			return null;
		} else {
			UsuariEntity usuari = usuariRepository.findOne(auditorActual);
			return usuari;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RipeaAuditorAware.class);

}
