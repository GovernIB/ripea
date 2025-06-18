/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Properties;

import javax.annotation.security.PermitAll;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface AplicacioService {

	void actualitzarEntitatThreadLocal(EntitatDto entitat);
	void actualitzarRolThreadLocal(String rol);

	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PermitAll
	public void processarAutenticacioUsuari(boolean comprovaAmbUsuariPlugin) throws NotFoundException;

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto getUsuariActual();
	
	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto updateUsuariActual(UsuariDto asDto);

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<IntegracioDto> integracioFindAll();

	@PreAuthorize("hasRole('IPA_SUPER')")
	public GenericDto integracioDiagnostic(String codi, DiagnosticFiltreDto filtre);
	
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
	public void excepcioSave(String uri, Throwable exception);

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
	@PreAuthorize("isAuthenticated()")
	public String propertyBaseUrl();

	/**
	 * Retorna els valors dels paràmetres de configuració de l'aplicació
	 * agrupades dins un grup determinat
	 * 
	 * @return els valors com a un objecte Properties.
	 */
	Properties propertiesFindByGroup(String codiGrup);

	@PreAuthorize("isAuthenticated()")
	String propertyFindByNom(String nom);

	List<UsuariDto> findUsuariAmbTextDades(String text);

	UsuariDto findUsuariCarrecAmbCodiDades(String codi);
	
	@PreAuthorize("isAuthenticated()")
	public Boolean propertyBooleanFindByKey(String key);

	@PreAuthorize("isAuthenticated()")
	boolean propertyBooleanFindByKey(
			String key,
			boolean defaultValueIfNull);

	@PreAuthorize("isAuthenticated()")
	public void setRolUsuariActual(String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<String> findUsuarisCodisAmbRol(String rol);

	@PreAuthorize("isAuthenticated()")
	public UsuariDto findUsuariAmbCodiDades(String codi);

	@PreAuthorize("isAuthenticated()")
	public void evictRolsDisponiblesEnAcls();

	@PreAuthorize("isAuthenticated()")
	boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull);

	@PreAuthorize("isAuthenticated()")
	public void evictRolsPerUsuari(String usuariCodi);

	@PreAuthorize("isAuthenticated()")
	public void evictCountAnotacionsPendents(String usuariCodi);

	public boolean mostrarLogsRendiment();

	@PreAuthorize("isAuthenticated()")
	public void actualitzarOrganCodi(String organCodi);

	@PreAuthorize("isAuthenticated()")
	public String getEntitatActualCodi();
	
	@PreAuthorize("isAuthenticated()")
	public Long getEntitatActualId();
	
	@PreAuthorize("isAuthenticated()")
	public String getOrganActualCodi();
	
	@PreAuthorize("isAuthenticated()")
	public String getRolActualCodi();
	
	@PreAuthorize("isAuthenticated()")
	public Long getOrganActualId();

	@PreAuthorize("isAuthenticated()")
	public String getValueForOrgan(String entitatCodi,
			String organCodi,
			String keyGeneral);

	@PreAuthorize("isAuthenticated()")
	public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi,
			String organCodi);

	@PreAuthorize("isAuthenticated()")
	public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi);

	@PreAuthorize("isAuthenticated()")
	public Properties getGroupPropertiesEntitatOrGeneral(
			String groupCode,
			String entitatCodi);

	@PreAuthorize("isAuthenticated()")
	public Properties getGroupPropertiesOrganOrEntitatOrGeneral(
			String groupCode,
			String entitatCodi,
			String organCodi);

	@PreAuthorize("isAuthenticated()")
	public boolean doesCurrentUserHasRol(
			String rol);
	
	@PreAuthorize("isAuthenticated()")
	public Long getProcedimentPerDefecte(Long entitatId, String rolActual);

	@PreAuthorize("isAuthenticated()")
	public boolean mostrarLogsCercadorAnotacio();

}