package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.AplicacioService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Properties;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioServiceEjb extends AbstractServiceEjb<AplicacioService> implements AplicacioService {

	@Delegate
	private AplicacioService delegateService;

	protected void setDelegateService(AplicacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public void actualitzarEntiatThreadLocal(EntitatDto entitat) {
		delegateService.actualitzarEntiatThreadLocal(entitat);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public void processarAutenticacioUsuari() {
		delegateService.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto getUsuariActual() {
		return delegateService.getUsuariActual();
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto updateUsuariActual(UsuariDto usuari) {
		return delegateService.updateUsuariActual(usuari);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegateService.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegateService.findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegateService.integracioFindAll();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		return delegateService.integracioFindDarreresAccionsByCodi(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodiPaginat(String codi, PaginacioParamsDto params, IntegracioFiltreDto filtre) {
		return delegateService.integracioFindDarreresAccionsByCodiPaginat(codi, params, filtre);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		delegateService.excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegateService.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegateService.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegateService.permisosFindRolsDistinctAll();
	}


	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyBaseUrl() {
		return delegateService.propertyBaseUrl();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public Properties propertiesFindByGroup(String codiGrup) {
		return delegateService.propertiesFindByGroup(codiGrup);
	}

	@Override
	public String propertyFindByNom(String nom) {
		return delegateService.propertyFindByNom(nom);
	}

	@Override
	public List<UsuariDto> findUsuariAmbTextDades(String text) {
		return delegateService.findUsuariAmbTextDades(text);
	}

	@Override
	public UsuariDto findUsuariCarrecAmbCodiDades(String codi) {
		return delegateService.findUsuariCarrecAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public Boolean propertyBooleanFindByKey(String key) {
		return delegateService.propertyBooleanFindByKey(key);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public boolean propertyBooleanFindByKey(
			String key,
			boolean defaultValueIfNull) {
		return delegateService.propertyBooleanFindByKey(
				key, 
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public void setRolUsuariActual(String rolActual) {
		delegateService.setRolUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<String> findUsuarisCodisAmbRol(String rol) {
		return delegateService.findUsuarisCodisAmbRol(rol);
	}

	@Override
	@RolesAllowed("tothom")
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		return delegateService.findUsuariAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public void evictRolsDisponiblesEnAcls() {
		delegateService.evictRolsDisponiblesEnAcls();
	}

	@Override
	@RolesAllowed("tothom")
	public boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull) {
		return delegateService.getBooleanJbossProperty(
				key,
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed("tothom")
	public void evictRolsPerUsuari(String usuariCodi) {
		delegateService.evictRolsPerUsuari(usuariCodi);
	}

    @Override
	@RolesAllowed("tothom")
    public void evictCountAnotacionsPendents(String usuariCodi) {
        delegateService.evictCountAnotacionsPendents(usuariCodi);
    }

    @Override
	public boolean mostrarLogsRendiment() {
		return delegateService.mostrarLogsRendiment();
	}

	@Override
	@RolesAllowed("tothom")
	public void actualitzarOrganCodi(
			String organCodi) {
		delegateService.actualitzarOrganCodi(organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public String getEntitatActualCodi() {
		return delegateService.getEntitatActualCodi();
	}

	@Override
	@RolesAllowed("tothom")
	public String getValueForOrgan(
			String entitatCodi,
			String organCodi,
			String keyGeneral) {
		return delegateService.getValueForOrgan(
				entitatCodi,
				organCodi,
				keyGeneral);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi,
			String organCodi) {
		return delegateService.getAllPropertiesOrganOrEntitatOrGeneral(entitatCodi, organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi) {
		return delegateService.getAllPropertiesEntitatOrGeneral(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getGroupPropertiesEntitatOrGeneral(
			String groupCode,
			String entitatCodi) {
		return delegateService.getGroupPropertiesEntitatOrGeneral(
				groupCode, 
				entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public Properties getGroupPropertiesOrganOrEntitatOrGeneral(
			String groupCode,
			String entitatCodi,
			String organCodi) {
		return delegateService.getGroupPropertiesOrganOrEntitatOrGeneral(
				groupCode,
				entitatCodi,
				organCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean doesCurrentUserHasRol(String rol) {
		return delegateService.doesCurrentUserHasRol(rol);
	}

	@Override
	@RolesAllowed("tothom")
	public Long getProcedimentPerDefecte(Long entitatId, String rolActual) {
		return delegateService.getProcedimentPerDefecte(entitatId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean mostrarLogsCercadorAnotacio() {
		return delegateService.mostrarLogsCercadorAnotacio();
	}

	@Override
	@RolesAllowed({ "IPA_SUPER" })
	public GenericDto integracioDiagnostic(String codi, DiagnosticFiltreDto filtre) {
		return delegateService.integracioDiagnostic(codi, filtre);
	}

}