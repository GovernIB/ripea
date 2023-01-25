package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.dto.config.OrganConfigDto;
import es.caib.ripea.core.api.exception.NotDefinedConfigException;
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

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return delegate.findEntitatsConfigByKey(key);
	}

	@Override
	public String getConfigValue(String configKey) throws NotDefinedConfigException {
		return delegate.getConfigValue(configKey);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void crearPropietatsConfigPerEntitats() {
		delegate.crearPropietatsConfigPerEntitats();
	}

	@Override
	public void actualitzarPropietatsJBossBdd() {
		delegate.actualitzarPropietatsJBossBdd();
	}
	
	@Override
	public void configurableEntitat(
			String key,
			boolean value) {
		delegate.configurableEntitat(
				key,
				value);
	}
	
	@Override
	public void configurableOrgan(
			String key,
			boolean value) {
		delegate.configurableOrgan(
				key,
				value);
		
	}
	
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void createPropertyOrgan(
			OrganConfigDto property) {
		delegate.createPropertyOrgan(property);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public PaginaDto<OrganConfigDto> findConfigsOrgans(
			String key,
			PaginacioParamsDto paginacioParams) {
		return delegate.findConfigsOrgans(
				key,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public OrganConfigDto findConfigOrgan(
			String key) {
		return delegate.findConfigOrgan(key);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void modificarPropertyOrgan(
			OrganConfigDto property) {
		delegate.modificarPropertyOrgan(property);
		
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void deletePropertyOrgan(
			String key) {
		delegate.deletePropertyOrgan(key);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ConfigDto findConfig(
			String key) {
		return delegate.findConfig(key);
	}
}
