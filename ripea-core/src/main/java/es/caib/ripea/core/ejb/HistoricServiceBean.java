package es.caib.ripea.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.service.HistoricService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class HistoricServiceBean implements HistoricService {

	@Autowired
	private HistoricService historicService;

	@Override
	@RolesAllowed({"IPA_API_HIST"})
	public void generateOldHistorics() {
		historicService.generateOldHistorics();
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return historicService.getPageDadesEntitat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, HistoricFiltreDto filtre) {
		return historicService.getDadesEntitat(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(
			HistoricFiltreDto filtre) {
		return historicService.getDadesOrgansGestors(filtre);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			HistoricFiltreDto filtre) {
		return historicService.getHistoricsByOrganGestor(filtre);
	}

	
	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre) {
		return historicService.getDadesUsuari(usuariCodi, filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre) {
		return historicService.getDadesInteressat(interessatDocNum, filtre);
	}
	
	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsEntitat(entitatId, filtre);
	}
	
	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(HistoricFiltreDto filtre) {
		return historicService.getDadesActualsOrgansGestors(filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsUsuari(codiUsuari, filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesActualsInteressat(String codiUsuari, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsInteressat(codiUsuari, filtre);
	}

}
