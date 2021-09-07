package es.caib.ripea.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

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
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			String rolActual,
			PaginacioParamsDto paginacioParams) {
		return historicService.getPageDadesEntitat(entitatId, filtre, rolActual, paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesEntitat(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(
			Long entitatId, 
			String rolActual,
			HistoricFiltreDto filtre) {
		return historicService.getDadesOrgansGestors(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			Long entitatId, 
			String rolActual, 
			HistoricFiltreDto filtre) {
		return historicService.getHistoricsByOrganGestor(entitatId, rolActual, filtre);
	}

	
	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesUsuari(usuariCodi, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesInteressat(interessatDocNum, entitatId, rolActual, filtre);
	}
	
	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsEntitat(entitatId, rolActual, filtre);
	}
	
	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsOrgansGestors(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsUsuari(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesActualsInteressat(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsInteressat(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public List<Long> comprovarAccesEstadistiques(Long entitatId, String rolActual) {
		return historicService.comprovarAccesEstadistiques(entitatId, rolActual);
	}

}
