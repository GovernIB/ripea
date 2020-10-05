package es.caib.ripea.core.ejb;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.HistoricDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.HistoricService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class HistoricServiceBean implements HistoricService {

	@Autowired
	private HistoricService historicService;

	@Override
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return historicService.getPageDadesEntitat(entitatId, filtre, paginacioParams);
	}

	@Override
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, HistoricFiltreDto filtre) {
		return historicService.getDadesEntitat(entitatId, filtre);
	}

	@Override
	public Map<Long, List<HistoricExpedientDto>> getDadesOrgansGestors(
			List<OrganGestorDto> organGestors,
			HistoricFiltreDto filtre) {
		return historicService.getDadesOrgansGestors(organGestors, filtre);
	}

	@Override
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre) {
		return historicService.getDadesUsuari(usuariCodi, filtre);
	}

	@Override
	public List<HistoricUsuariDto> getDadesUsuariActual(HistoricFiltreDto filtre) {
		return getDadesUsuariActual(filtre);
	}

	@Override
	public List<HistoricDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

}
