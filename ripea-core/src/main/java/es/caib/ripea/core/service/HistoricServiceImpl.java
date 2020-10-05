package es.caib.ripea.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.HistoricDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.HistoricExpedientRepository;
import es.caib.ripea.core.repository.HistoricUsuariRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.task.HistoricTask;

@Service
public class HistoricServiceImpl implements HistoricService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private HistoricExpedientRepository historicExpedientRepository;
	@Autowired
	private HistoricUsuariRepository historicUsuariRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private HistoricTask historicTask;

	@Override
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
//		historicTask.registreDiari();
//		historicTask.registreMensual();
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		Page<HistoricExpedientAggregation> pagina = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi(), 
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<HistoricExpedientDto> historicEntitatDto = paginacioHelper.toPaginaDto(
				pagina,
				HistoricExpedientDto.class);
		return historicEntitatDto;
	}

	@Override
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricExpedientAggregation> historicEntitat = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		return conversioTipusHelper.convertirList(historicEntitat, HistoricExpedientDto.class);
	}

	@Override
	public Map<Long, List<HistoricExpedientDto>> getDadesOrgansGestors(
			List<OrganGestorDto> organGestors,
			HistoricFiltreDto filtre) {
		if (organGestors == null || organGestors.isEmpty()) {
			return null;
		}
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;
		Map<Long, List<HistoricExpedientDto>> results = new HashMap<Long, List<HistoricExpedientDto>>();
		for (OrganGestorDto organDto : organGestors) {
			List<HistoricExpedientAggregation> historic = historicExpedientRepository.findByOrganGestorAndDateRangeGroupedByDate(
					organGestorRepository.findOne(organDto.getId()),
					filtre.getTipusAgrupament(),
					!fiteringByMetaExpedients,
					!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
					filtre.getDataInici(),
					filtre.getDataFi());
			results.put(organDto.getId(), conversioTipusHelper.convertirList(historic, HistoricExpedientDto.class));	
		}
		return results;
	}

	@Override
	public List<HistoricUsuariDto> getDadesUsuari(
			String usuariCodi,
			HistoricFiltreDto filtre) {
		UsuariEntity usuari = usuariRepository.findByCodi(usuariCodi);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricUsuariAggregation> historic = historicUsuariRepository.findByDateRangeGroupedByDate(
				usuari,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		
		return conversioTipusHelper.convertirList(historic, HistoricUsuariDto.class);
	}

	@Override
	public List<HistoricUsuariDto> getDadesUsuariActual(
			HistoricFiltreDto filtre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return getDadesUsuari(auth.getName(), filtre);
	}

	@Override
	public List<HistoricDto> getDadesInteressat(
			String interessatDocNum,
			HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

}
