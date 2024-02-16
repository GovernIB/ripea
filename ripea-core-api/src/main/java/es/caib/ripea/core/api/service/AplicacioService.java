/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;
import java.util.Properties;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.IntegracioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	void actualitzarEntiatThreadLocal(EntitatDto entitat);

	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public void processarAutenticacioUsuari() throws NotFoundException;

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public UsuariDto getUsuariActual();
	
	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public UsuariDto updateUsuariActual(UsuariDto asDto);

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<IntegracioDto> integracioFindAll();

	/**
	 * Obté la llista de les darreres accions realitzades a una integració.
	 * 
	 * @param codi Codi de la integració.
	 * @return La llista amb les darreres accions.
	 * @throws NotFoundException Si no s'ha trobat la integració amb el codi especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) throws NotFoundException;

	/**
	 * Obté la llista de les darreres accions realitzades a una integració.
	 *
	 * @param codi Codi de la integració.
	 * @param params Parametres de la paginació.
	 * @return La llista amb les darreres accions paginada.
	 * @throws NotFoundException Si no s'ha trobat la integració amb el codi especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodiPaginat(String codi, PaginacioParamsDto params, IntegracioFiltreDto filtre);

	/**
	 * Emmagatzema una excepció llençada per un servei.
	 * 
	 * @param exception
	 *             L'excepció a emmagatzemar.
	 */
	public void excepcioSave(Throwable exception);

	/**
	 * Consulta la informació d'una excepció donat el seu índex.
	 * 
	 * @param index
	 *             L'index de l'excepció.
	 * @return L'excepció.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public ExcepcioLogDto excepcioFindOne(Long index);

	/**
	 * Retorna una llista amb les darreres excepcions emmagatzemades.
	 * 
	 * @return La llista amb les darreres excepcions.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<ExcepcioLogDto> excepcioFindAll();

	/**
	 * Retorna una llista amb els diferents rols els quals
	 * tenen assignat algun permis.
	 * 
	 * @return La llista amb els rols.
	 */
	public List<String> permisosFindRolsDistinctAll();

	/**
	 * Retorna el valor de la propietat es.caib.ripea.base.url.
	 * 
	 * @return el valor del paràmetre.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public String propertyBaseUrl();


	/**
	 * Retorna el valor de la propietat es.caib.ripea.plugin.escaneig..ids.
	 * 
	 * @return el valor del paràmetre.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public String propertyPluginEscaneigIds();

	/**
	 * Retorna els valors dels paràmetres de configuració de l'aplicació
	 * agrupades dins un grup determinat
	 * 
	 * @return els valors com a un objecte Properties.
	 */
	Properties propertiesFindByGroup(String codiGrup);

	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	String propertyFindByNom(String nom);

	List<UsuariDto> findUsuariAmbTextDades(String text);

	UsuariDto findUsuariCarrecAmbCodiDades(String codi);
	
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public Boolean propertyBooleanFindByKey(String key);

	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	boolean propertyBooleanFindByKey(
			String key,
			boolean defaultValueIfNull);

	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public void setRolUsuariActual(String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<String> findUsuarisCodisAmbRol(String rol);

	@PreAuthorize("hasRole('tothom')")
	public UsuariDto findUsuariAmbCodiDades(String codi);

	@PreAuthorize("hasRole('tothom')")
	public void evictRolsDisponiblesEnAcls();

	@PreAuthorize("hasRole('tothom')")
	boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull);

	@PreAuthorize("hasRole('tothom')")
	public void evictRolsPerUsuari(String usuariCodi);

	public boolean mostrarLogsRendiment();

	@PreAuthorize("hasRole('tothom')")
	public void actualitzarOrganCodi(
			String organCodi);

	@PreAuthorize("hasRole('tothom')")
	public String getEntitatActualCodi();

	@PreAuthorize("hasRole('tothom')")
	public String getValueForOrgan(String entitatCodi,
			String organCodi,
			String keyGeneral);

	@PreAuthorize("hasRole('tothom')")
	public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi,
			String organCodi);

	@PreAuthorize("hasRole('tothom')")
	public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi);

	@PreAuthorize("hasRole('tothom')")
	public Properties getGroupPropertiesEntitatOrGeneral(
			String groupCode,
			String entitatCodi);

	@PreAuthorize("hasRole('tothom')")
	public Properties getGroupPropertiesOrganOrEntitatOrGeneral(
			String groupCode,
			String entitatCodi,
			String organCodi);

	@PreAuthorize("hasRole('tothom')")
	public boolean doesCurrentUserHasRol(
			String rol);

	@PreAuthorize("hasRole('tothom')")
	public Long getProcedimentPerDefecte();

}