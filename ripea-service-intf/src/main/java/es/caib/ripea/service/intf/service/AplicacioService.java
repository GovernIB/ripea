package es.caib.ripea.service.intf.service;

import java.util.List;
import java.util.Properties;

import javax.annotation.security.PermitAll;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.dto.IntegracioEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

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
	 * Obté el detall d'una acció d'integració donat el seu identificador.
	 *
	 * @param id Identificador de l'acció.
	 * @return L'acció d'integració, o null si no existeix.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	IntegracioAccioDto integracioFindOne(Long id);

	public List<IntegracioAccioDto> getLastIntegracions(IntegracioEnumDto codiIntegracio, int numElements);
	
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
	 * Retorna una pàgina d'excepcions emmagatzemades.
	 *
	 * @param params
	 *             Paràmetres de paginació i ordenació.
	 * @return La pàgina d'excepcions.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<ExcepcioLogDto> excepcioFindPage(PaginacioParamsDto params);

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

	/**
	 * Resol en una sola crida (2 consultes batch a BD) un conjunt de propietats, amb la mateixa
	 * semàntica de resolució (òrgan → entitat → general) que {@link #propertyFindByNom(String)}.
	 */
	@PreAuthorize("isAuthenticated()")
	Properties getConfigs(List<String> keys);

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

	@PreAuthorize("isAuthenticated()")
	public void setEntitatActual(Long entitatId);

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
	
	@PreAuthorize("hasRole('IPA_SUPER')")
	public Long updateUsuariCodi(String codiAntic, String codiNou);
	
	@PermitAll
	public MeterRegistry getMeterRegistry();
	
	@PermitAll
	public void stopTimer(Timer.Sample sample, String metricCode, String... tags);
	
	@PermitAll
	public String getMetriquesJSON() throws Exception;
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<Long> getPortafirmesEliminats();
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public String executePortafirmesEliminat(Long tascaId) throws Exception;
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<Long> getTasquesComanda();
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public String executeTascaComanda(Long tascaId) throws Exception;
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<Long> getAvisosComanda();

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public String executeAvisComanda(Long expedientId) throws Exception;

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<Long> getEntitatsSenseTipusDocumentals();

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public String executeCrearTipusDocumentalsEntitat(Long entitatId) throws Exception;

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<String> getConfigsAmbEntitat();

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public String executeEliminaConfigOrfe(String key) throws Exception;
}