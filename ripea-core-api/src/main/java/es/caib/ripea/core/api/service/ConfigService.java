package es.caib.ripea.core.api.service;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.dto.config.OrganConfigDto;
import es.caib.ripea.core.api.exception.NotDefinedConfigException;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

	/**
	 * Actualitza el valor d'una propietat de configuració.
	 *
	 * @param property Informació que es vol actualitzar.
	 * @return El DTO amb les dades modificades.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	ConfigDto updateProperty(ConfigDto property) throws Exception;

	/**
	 * Consulta totes les propietats de configuració de l'aplicació.
	 * Retorna les propietats estructurades en forma d'arbres segons al grup o subgrups al que pertanyen.
	 * Cada arbre està format per un grup de propietats que no pertany a cap altre grup.
	 *
	 * @return Retorna un llistat de tots els grups de propietats que no pertanyen a cap grup.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	List<ConfigGroupDto> findAll();


	/**
	 * Procediment que actualitza totes les propietats de configuració per a configurar-les amb
	 * el valor de les properties configurades a JBoss.
	 *
	 * L'execució d'aquest procés només actualitza el valor de les properties que ja existeixen a la base de dades
	 * i que a més també estan definides al fitxer JBoss.
	 *
	 * @return Llistat de les properties editades.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	List<String> syncFromJBossProperties();

	@PreAuthorize("hasRole('IPA_SUPER')")
	List<ConfigDto> findEntitatsConfigByKey(String key);

	String getConfigValue(String configKey) throws NotDefinedConfigException;

	@PreAuthorize("hasRole('IPA_SUPER')")
	void crearPropietatsConfigPerEntitats();

	@PreAuthorize("hasRole('IPA_SUPER')")
	void actualitzarPropietatsJBossBdd();

	@PreAuthorize("hasRole('IPA_SUPER')")
	public void configurableEntitat(
			String key,
			boolean value);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public void configurableOrgan(
			String key,
			boolean value);


	@PreAuthorize("hasRole('IPA_SUPER')")
	public void createPropertyOrgan(
			OrganConfigDto property);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<OrganConfigDto> findConfigsOrgans(
			String key,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public OrganConfigDto findConfigOrgan(
			String key);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public void modificarPropertyOrgan(
			OrganConfigDto property);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public void deletePropertyOrgan(
			String key);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public ConfigDto findConfig(
			String key);
}

