/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;
import java.util.Properties;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.IntegracioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;

	@Override
	public void actualitzarEntiatThreadLocal(EntitatDto entitat) {
		delegate.actualitzarEntiatThreadLocal(entitat);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public void processarAutenticacioUsuari() {
		delegate.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto getUsuariActual() {
		return delegate.getUsuariActual();
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto updateUsuariActual(UsuariDto usuari) {
		return delegate.updateUsuariActual(usuari);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegate.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegate.findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		return delegate.integracioFindDarreresAccionsByCodi(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodiPaginat(String codi, PaginacioParamsDto params, IntegracioFiltreDto filtre) {
		return delegate.integracioFindDarreresAccionsByCodiPaginat(codi, params, filtre);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		delegate.excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegate.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegate.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegate.permisosFindRolsDistinctAll();
	}


	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyBaseUrl() {
		return delegate.propertyBaseUrl();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyPluginEscaneigIds() {
		return delegate.propertyPluginEscaneigIds();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public Properties propertiesFindByGroup(String codiGrup) {
		return delegate.propertiesFindByGroup(codiGrup);
	}

	@Override
	public String propertyFindByNom(String nom) {
		return delegate.propertyFindByNom(nom);
	}

	@Override
	public List<UsuariDto> findUsuariAmbTextDades(String text) {
		return delegate.findUsuariAmbTextDades(text);
	}

	@Override
	public UsuariDto findUsuariCarrecAmbCodiDades(String codi) {
		return delegate.findUsuariCarrecAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public Boolean propertyBooleanFindByKey(String key) {
		return delegate.propertyBooleanFindByKey(key);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public boolean propertyBooleanFindByKey(
			String key,
			boolean defaultValueIfNull) {
		return delegate.propertyBooleanFindByKey(
				key, 
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public void setRolUsuariActual(String rolActual) {
		delegate.setRolUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<String> findUsuarisCodisAmbRol(String rol) {
		return delegate.findUsuarisCodisAmbRol(rol);
	}

	@Override
	@RolesAllowed("tothom")
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		return delegate.findUsuariAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public void evictRolsDisponiblesEnAcls() {
		delegate.evictRolsDisponiblesEnAcls();
	}

	@Override
	@RolesAllowed("tothom")
	public boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull) {
		return delegate.getBooleanJbossProperty(
				key,
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed("tothom")
	public void evictRolsPerUsuari(String usuariCodi) {
		delegate.evictRolsPerUsuari(usuariCodi);		
	}

	@Override
	public boolean mostrarLogsRendiment() {
		return delegate.mostrarLogsRendiment();
	}

	@Override
	@RolesAllowed("tothom")
	public void actualitzarOrganCodi(
			String organCodi) {
		delegate.actualitzarOrganCodi(organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public String getEntitatActualCodi() {
		return delegate.getEntitatActualCodi();
	}

	@Override
	@RolesAllowed("tothom")
	public String getValueForOrgan(
			String entitatCodi,
			String organCodi,
			String keyGeneral) {
		return delegate.getValueForOrgan(
				entitatCodi,
				organCodi,
				keyGeneral);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi,
			String organCodi) {
		return delegate.getAllPropertiesOrganOrEntitatOrGeneral(entitatCodi, organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi) {
		return delegate.getAllPropertiesEntitatOrGeneral(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getGroupPropertiesEntitatOrGeneral(
			String groupCode,
			String entitatCodi) {
		return delegate.getGroupPropertiesEntitatOrGeneral(
				groupCode, 
				entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getGroupPropertiesOrganOrEntitatOrGeneral(
			String groupCode,
			String entitatCodi,
			String organCodi) {
		return delegate.getGroupPropertiesOrganOrEntitatOrGeneral(
				groupCode,
				entitatCodi,
				organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean doesCurrentUserHasRol(String rol) {
		return delegate.doesCurrentUserHasRol(rol);
	}


	@Override
	@RolesAllowed("tothom")
	public Long getProcedimentPerDefecte(Long entitatId, String rolActual) {
		return delegate.getProcedimentPerDefecte(entitatId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean mostrarLogsCercadorAnotacio() {
		return delegate.mostrarLogsCercadorAnotacio();
	}
	
}