/**
 * 
 */
package es.caib.ripea.war.security;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

/**
 * Aconsegueix els rols que seran rellevants per a l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	//private ApplicationContext applicationContext;
	//private ServeiService serveiService;
	private Set<String> defaultMappableAttributes;
	private Set<String> mappableAttributes = new HashSet<String>();



	public Set<String> getMappableAttributes() {
		refrescarMappableAttributes();
		return mappableAttributes;
	}

	public void setDefaultMappableAttributes(Set<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}

	/*@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}*/



	private void refrescarMappableAttributes() {
		LOGGER.debug("Refrescant el llistat de rols per mapejar");
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
		/*if (serveiService == null) {
			LOGGER.debug("El serveiService és null. Obtenint el serveiService mitjançant l'applicationContext");
			serveiService = applicationContext.getBean(ServeiService.class);
		}
		if (serveiService != null) {
			mappableAttributes.addAll(serveiService.getRolsConfigurats());
		} else {
			LOGGER.error("No s'han pogut obtenir els rols addicionals del serveiService: El service és null");
		}*/
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedMappableAttributesRetriever.class);

}
