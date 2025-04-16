package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.AplicacioService;
import lombok.experimental.Delegate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Properties;

@Stateless
public class AplicacioServiceEjb extends AbstractServiceEjb<AplicacioService> implements AplicacioService {

	@Delegate private AplicacioService delegateService;

	protected void setDelegateService(AplicacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public void actualitzarEntiatThreadLocal(EntitatDto entitat) {
		delegateService.actualitzarEntiatThreadLocal(entitat);
	}

	@Override
	@PermitAll
	public void processarAutenticacioUsuari(boolean comprovaAmbUsuariPlugin) {
		delegateService.processarAutenticacioUsuari(comprovaAmbUsuariPlugin);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getUsuariActual() {
		return delegateService.getUsuariActual();
	}
	
	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuariActual(UsuariDto usuari) {
		return delegateService.updateUsuariActual(usuari);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegateService.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public void excepcioSave(String uri, Throwable exception) {
		delegateService.excepcioSave(uri, exception);
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
	@RolesAllowed("**")
	public List<String> permisosFindRolsDistinctAll() {
		return delegateService.permisosFindRolsDistinctAll();
	}


	@Override
	@RolesAllowed("**")
	public String propertyBaseUrl() {
		return delegateService.propertyBaseUrl();
	}

	@Override
	@RolesAllowed("**")
	public Properties propertiesFindByGroup(String codiGrup) {
		return delegateService.propertiesFindByGroup(codiGrup);
	}

	@Override
	@RolesAllowed("**")
	public String propertyFindByNom(String nom) {
		return delegateService.propertyFindByNom(nom);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbTextDades(String text) {
		return delegateService.findUsuariAmbTextDades(text);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariCarrecAmbCodiDades(String codi) {
		return delegateService.findUsuariCarrecAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed("**")
	public Boolean propertyBooleanFindByKey(String key) {
		return delegateService.propertyBooleanFindByKey(key);
	}

	@Override
	@RolesAllowed("**")
	public boolean propertyBooleanFindByKey(
			String key,
			boolean defaultValueIfNull) {
		return delegateService.propertyBooleanFindByKey(
				key, 
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed("**")
	public void setRolUsuariActual(String rolActual) {
		delegateService.setRolUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<String> findUsuarisCodisAmbRol(String rol) {
		return delegateService.findUsuarisCodisAmbRol(rol);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariAmbCodiDades(String codi) {
		return delegateService.findUsuariAmbCodiDades(codi);
	}

	@Override
	@RolesAllowed("**")
	public void evictRolsDisponiblesEnAcls() {
		delegateService.evictRolsDisponiblesEnAcls();
	}

	@Override
	@RolesAllowed("**")
	public boolean getBooleanJbossProperty(
			String key,
			boolean defaultValueIfNull) {
		return delegateService.getBooleanJbossProperty(
				key,
				defaultValueIfNull);
	}

	@Override
	@RolesAllowed("**")
	public void evictRolsPerUsuari(String usuariCodi) {
		delegateService.evictRolsPerUsuari(usuariCodi);
	}

    @Override
	@RolesAllowed("**")
    public void evictCountAnotacionsPendents(String usuariCodi) {
        delegateService.evictCountAnotacionsPendents(usuariCodi);
    }

    @Override
    @RolesAllowed("**")
	public boolean mostrarLogsRendiment() {
		return delegateService.mostrarLogsRendiment();
	}

	@Override
	@RolesAllowed("**")
	public void actualitzarOrganCodi(
			String organCodi) {
		delegateService.actualitzarOrganCodi(organCodi);
	}

	@Override
	@RolesAllowed("**")
	public String getEntitatActualCodi() {
		return delegateService.getEntitatActualCodi();
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public Properties getAllPropertiesOrganOrEntitatOrGeneral(String entitatCodi,
			String organCodi) {
		return delegateService.getAllPropertiesOrganOrEntitatOrGeneral(entitatCodi, organCodi);
	}

	@Override
	@RolesAllowed("**")
	public Properties getAllPropertiesEntitatOrGeneral(String entitatCodi) {
		return delegateService.getAllPropertiesEntitatOrGeneral(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public Properties getGroupPropertiesEntitatOrGeneral(
			String groupCode,
			String entitatCodi) {
		return delegateService.getGroupPropertiesEntitatOrGeneral(
				groupCode, 
				entitatCodi);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public boolean doesCurrentUserHasRol(String rol) {
		return delegateService.doesCurrentUserHasRol(rol);
	}

	@Override
	@RolesAllowed("**")
	public Long getProcedimentPerDefecte(Long entitatId, String rolActual) {
		return delegateService.getProcedimentPerDefecte(entitatId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public boolean mostrarLogsCercadorAnotacio() {
		return delegateService.mostrarLogsCercadorAnotacio();
	}

	@Override
	@RolesAllowed({ "IPA_SUPER" })
	public GenericDto integracioDiagnostic(String codi, DiagnosticFiltreDto filtre) {
		return delegateService.integracioDiagnostic(codi, filtre);
	}

}