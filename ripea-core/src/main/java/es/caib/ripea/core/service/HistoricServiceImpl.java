package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.HistoricDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
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
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
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
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
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
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getDadesOrgansGestors(HistoricFiltreDto filtre) {
		List<Long> organGestors = filtre.getOrganGestorsIds();
		if (organGestors == null || organGestors.isEmpty()) {
			return null;
		}
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		Map<OrganGestorDto, List<HistoricExpedientDto>> results = new HashMap<>();
		for (Long organId : organGestors) {
			OrganGestorEntity organGestor = organGestorRepository.findOne(organId);
			List<HistoricExpedientAggregation> historic = historicExpedientRepository.findByOrganGestorAndDateRangeGroupedByDate(
					organGestor,
					filtre.getTipusAgrupament(),
					!fiteringByMetaExpedients,
					!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
					filtre.getDataInici(),
					filtre.getDataFi());
			results.put(
					conversioTipusHelper.convertir(organGestor, OrganGestorDto.class),
					conversioTipusHelper.convertirList(historic, HistoricExpedientDto.class));
		}
		return results;
	}

	@Override
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre) {
		UsuariEntity usuari = usuariRepository.findByCodi(usuariCodi);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
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
	
//	@Override
//	public List<HistoricUsuariDto> getDadesUsuariActual(HistoricFiltreDto filtre) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		return getDadesUsuari(auth.getName(), filtre);
//	}

	@Override
	public List<HistoricDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

		Collection<HistoricExpedientEntity> historics = historicTask.calcularHistoricExpedient(
				(new LocalDate()).toDateTimeAtStartOfDay().toDate(),
				(new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				HistoricTipusEnumDto.DIARI);

		HistoricExpedientDto sumatori = new HistoricExpedientDto(
				null,
				(new LocalDate()).toDateTimeAtCurrentTime().toDate());
		for (HistoricExpedientEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !fiteringByMetaExpedients ||
					filtre.getMetaExpedientsIds().contains(metaExpedient.getId());
			boolean selectedByOrgan = !fiteringByOrganGestors ||
					filtre.getOrganGestorsIds().contains(historic.getOrganGestor().getId());
			if (selectedByMetaExp && selectedByOrgan && metaExpedient.getEntitat().equals(entitat)) {
				HistoricExpedientDto aux = new HistoricExpedientDto();
				aux.setNumExpedientsCreats(historic.getNumExpedientsCreats());
				aux.setNumExpedientsCreatsTotal(historic.getNumExpedientsCreatsTotal());
				aux.setNumExpedientsTancats(historic.getNumExpedientsCreats());
				aux.setNumExpedientsTancatsTotal(historic.getNumExpedientsCreats());

//				aux.setNumExpedientsAmbAlertes(historic.getNumExpedientsCreats());
//				aux.setNumExpedientsAmbErrorsValidacio(historic.getNumExpedientsCreats());
//				aux.setNumDocsPendentsSignar(historic.getNumExpedientsCreats());
				aux.setNumDocsSignats(historic.getNumExpedientsCreats());
//				aux.setNumDocsPendentsNotificar(historic.getNumExpedientsCreats());
				aux.setNumDocsNotificats(historic.getNumExpedientsCreats());
				
				sumatori.combinarAmb(aux);
			}
		}
		return conversioTipusHelper.convertirList(
				new ArrayList<HistoricExpedientEntity>(historics),
				HistoricExpedientDto.class);
	}

	@Transactional
	@Override
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(HistoricFiltreDto filtre) {
		List<Long> organGestors = filtre.getOrganGestorsIds();
		if (organGestors == null || organGestors.isEmpty()) {
			return null;
		}
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		Collection<HistoricExpedientEntity> historics = historicTask.calcularHistoricExpedient(
				(new LocalDate()).toDateTimeAtStartOfDay().toDate(),
				(new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				HistoricTipusEnumDto.DIARI);
		Map<OrganGestorDto, HistoricExpedientDto> results = new HashMap<>();
		for (Long organId : organGestors) {
			OrganGestorEntity organGestor = organGestorRepository.findOne(organId);
			HistoricExpedientDto sumatori = new HistoricExpedientDto(
					null,
					(new LocalDate()).toDateTimeAtCurrentTime().toDate());
			for (HistoricExpedientEntity historic : historics) {
				MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
				boolean selectedByMetaExp = !fiteringByMetaExpedients ||
						filtre.getMetaExpedientsIds().contains(metaExpedient.getId());

				if (selectedByMetaExp && metaExpedient.getOrganGestor() != null &&
						metaExpedient.getOrganGestor().getId() == organId) {
					HistoricExpedientDto aux = new HistoricExpedientDto();
					aux.setNumExpedientsCreats(historic.getNumExpedientsCreats());
					aux.setNumExpedientsCreatsTotal(historic.getNumExpedientsCreatsTotal());
					aux.setNumExpedientsTancats(historic.getNumExpedientsCreats());
					aux.setNumExpedientsTancatsTotal(historic.getNumExpedientsCreats());

//					aux.setNumExpedientsAmbAlertes(historic.getNumExpedientsCreats());
//					aux.setNumExpedientsAmbErrorsValidacio(historic.getNumExpedientsCreats());
//					aux.setNumDocsPendentsSignar(historic.getNumExpedientsCreats());
					aux.setNumDocsSignats(historic.getNumExpedientsCreats());
//					aux.setNumDocsPendentsNotificar(historic.getNumExpedientsCreats());
					aux.setNumDocsNotificats(historic.getNumExpedientsCreats());
					
					sumatori.combinarAmb(aux);
				}
			}
			results.put(
					conversioTipusHelper.convertir(organGestor, OrganGestorDto.class),
					sumatori);
		}

		return results;
	}

}
