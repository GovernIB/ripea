package es.caib.ripea.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.historic.HistoricExpedientDto;
import es.caib.ripea.service.intf.dto.historic.HistoricFiltreDto;
import es.caib.ripea.service.intf.dto.historic.HistoricInteressatDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricUsuariDto;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer.RegistresUsuariDiari;
import es.caib.ripea.service.intf.service.HistoricService;
import lombok.experimental.Delegate;

@Stateless
public class HistoricServiceEjb extends AbstractServiceEjb<HistoricService> implements HistoricService {

	@Delegate private HistoricService delegateService;

	protected void setDelegateService(HistoricService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"IPA_API_HIST"})
	public void generateOldHistorics() {
		delegateService.generateOldHistorics();
	}
	
	@Override
	@RolesAllowed("**")
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			String rolActual,
			PaginacioParamsDto paginacioParams) {
		return delegateService.getPageDadesEntitat(entitatId, filtre, rolActual, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesEntitat(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(
			Long entitatId, 
			String rolActual,
			HistoricFiltreDto filtre) {
		return delegateService.getDadesOrgansGestors(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<HistoricOrganGestorSerializer.RegistresOrganGestor> getRegistresDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return delegateService.getRegistresDadesOrgansGestors(entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed("**")
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			Long entitatId, 
			String rolActual, 
			HistoricFiltreDto filtre) {
		return delegateService.getHistoricsByOrganGestor(entitatId, rolActual, filtre);
	}

	
	@Override
	@RolesAllowed("**")
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesUsuari(usuariCodi, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistresUsuariDiari> getRegistresDadesUsuaris(List<String> usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return delegateService.getRegistresDadesUsuaris(usuarisCodi, entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed("**")
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesInteressat(interessatDocNum, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistresInteressatDiari> getRegistresDadesInteressat(List<String> interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return delegateService.getRegistresDadesInteressat(interessatsDocNum, entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed("**")
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesActualsEntitat(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesActualsOrgansGestors(entitatId, rolActual, filtre);
	}

    @Override
	@RolesAllowed("**")
    public List<RegistreOrganGestor> getRegistresDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
        return delegateService.getRegistresDadesActualsOrgansGestors(entitatId, rolActual, filtre);
    }

    @Override
	@RolesAllowed("**")
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesActualsUsuari(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<HistoricInteressatDto> getDadesActualsInteressat(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return delegateService.getDadesActualsInteressat(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> comprovarAccesEstadistiques(Long entitatId, String rolActual) {
		return delegateService.comprovarAccesEstadistiques(entitatId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportarHistoricEntitat(EntitatDto entitat, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return delegateService.exportarHistoricEntitat(entitat, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return delegateService.exportarHistoricOrgansGestors(entitatId, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportarHistoricUsuaris(String[] usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return delegateService.exportarHistoricUsuaris(usuarisCodi, entitatId, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportarHistoricInteressats(String[] interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return delegateService.exportarHistoricInteressats(interessatsDocNum, entitatId, rolActual, filtre, format);
	}

}
