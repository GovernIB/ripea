package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConfigServiceBean implements ConfigService {

	@Autowired
	ConfigService delegate;

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegate.updateProperty(property);
	}
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ConfigGroupDto> findAll(){
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<String> syncFromJBossProperties(){
		return delegate.syncFromJBossProperties();
	}
}
