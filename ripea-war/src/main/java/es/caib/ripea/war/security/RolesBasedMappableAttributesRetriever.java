/**
 * 
 */
package es.caib.ripea.war.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

import es.caib.ripea.core.api.service.AplicacioService;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever, ApplicationContextAware {

	private Set<String> defaultMappableAttributes;
	private Set<String> mappableAttributes = new HashSet<String>();
	private ApplicationContext applicationContext;

	public Set<String> getMappableAttributes() {
		// Aquest mètode es crida cada vegada que un usuari s'autentica
		refrescarMappableAttributes(true);
		return mappableAttributes;
	}

	public void setDefaultMappableAttributes(Set<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	private void refrescarMappableAttributes(boolean forceRefresh) {
		LOGGER.debug("Refrescant el llistat de rols per mapejar");
		mappableAttributes.clear();
		if (defaultMappableAttributes != null) {
			mappableAttributes.addAll(defaultMappableAttributes);
		}
		try {
			AplicacioService aplicacioService = applicationContext.getBean(AplicacioService.class);
			List<String> rolsPermisos = aplicacioService.permisosFindRolsDistinctAll();
			mappableAttributes.addAll(rolsPermisos);
			String rolsPerMostrar = Arrays.toString(mappableAttributes.toArray(new String[mappableAttributes.size()]));
			LOGGER.debug("Rols disponibles: " + rolsPerMostrar);
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
