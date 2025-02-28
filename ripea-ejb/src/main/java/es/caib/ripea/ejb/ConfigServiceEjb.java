package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.config.ConfigDto;
import es.caib.ripea.service.intf.dto.config.ConfigGroupDto;
import es.caib.ripea.service.intf.dto.config.OrganConfigDto;
import es.caib.ripea.service.intf.exception.NotDefinedConfigException;
import es.caib.ripea.service.intf.service.ConfigService;
import lombok.experimental.Delegate;

@Stateless
public class ConfigServiceEjb extends AbstractServiceEjb<ConfigService> implements ConfigService {

	@Delegate private ConfigService delegateService;

	protected void setDelegateService(ConfigService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegateService.updateProperty(property);
	}
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ConfigGroupDto> findAll(){
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void resetPlugin(String pluginCode) {
		delegateService.resetPlugin(pluginCode);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<String> syncFromJBossProperties(){
		return delegateService.syncFromJBossProperties();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return delegateService.findEntitatsConfigByKey(key);
	}

	@Override
	public String getConfigValue(String configKey) throws NotDefinedConfigException {
		return delegateService.getConfigValue(configKey);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void crearPropietatsConfigPerEntitats() {
		delegateService.crearPropietatsConfigPerEntitats();
	}

	@Override
	public void actualitzarPropietatsJBossBdd() {
		delegateService.actualitzarPropietatsJBossBdd();
	}
	
	@Override
	public void configurableEntitat(
			String key,
			boolean value) {
		delegateService.configurableEntitat(
				key,
				value);
	}
	
	@Override
	public void configurableOrgan(
			String key,
			boolean value) {
		delegateService.configurableOrgan(
				key,
				value);
		
	}
	
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void createPropertyOrgan(
			OrganConfigDto property) {
		delegateService.createPropertyOrgan(property);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public PaginaDto<OrganConfigDto> findConfigsOrgans(
			String key,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findConfigsOrgans(
				key,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public OrganConfigDto findConfigOrgan(
			String key) {
		return delegateService.findConfigOrgan(key);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void modificarPropertyOrgan(
			OrganConfigDto property) {
		delegateService.modificarPropertyOrgan(property);
		
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public void deletePropertyOrgan(
			String key) {
		delegateService.deletePropertyOrgan(key);
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ConfigDto findConfig(
			String key) {
		return delegateService.findConfig(key);
	}
}
