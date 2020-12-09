package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.HistoricAggregation;
import es.caib.ripea.core.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.core.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.HistoricInteressatEntity;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.HistoricExpedientRepository;
import es.caib.ripea.core.repository.HistoricInteressatRepository;
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
	private HistoricInteressatRepository historicInteressatRepository;
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		Page<HistoricExpedientAggregation> pagina = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricExpedientAggregation> historicEntitat = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		return conversioTipusHelper.convertirList(historicEntitat, HistoricExpedientDto.class);
	}

	@Override
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(HistoricFiltreDto filtre) {
		List<Long> organGestorIds = filtre.getOrganGestorsIds();
		if (organGestorIds == null) {
			return new HashMap<>();
		}
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> results = new HashMap<>();
		List<OrganGestorDto> organGestors = new ArrayList<>();
		for (Long organId : organGestorIds) {
			OrganGestorEntity organGestor = organGestorRepository.findOne(organId);
			List<HistoricExpedientAggregation> historics = historicExpedientRepository.findByOrganGestorAndDateRangeGroupedByDate(
					organGestor,
					filtre.getTipusAgrupament(),
					!fiteringByMetaExpedients,
					!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
					filtre.getDataInici(),
					filtre.getDataFi());
			OrganGestorDto organDto = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
			organGestors.add(organDto);
			
			for (HistoricExpedientAggregation historic : historics) {
				Date key = historic.getData();
				if (!results.containsKey(key)) {
					results.put(key, new HashMap<OrganGestorDto, HistoricExpedientDto>());
				}
				Map<OrganGestorDto, HistoricExpedientDto> mapOrganGestors = results.get(key);	
				mapOrganGestors.put(
						organDto,
						conversioTipusHelper.convertir(historic, HistoricExpedientDto.class));
			}			
		}
		if (filtre.getIncorporarExpedientsComuns()) {
			OrganGestorDto organDto = new OrganGestorDto();
			organDto.setCodi("");
			organDto.setNom("Expedients comuns");
			List<HistoricExpedientAggregation> historics = historicExpedientRepository.findByExpedientsComunsAndDateRangeGroupedByDate(
					filtre.getTipusAgrupament(),
					!fiteringByMetaExpedients,
					!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
					filtre.getDataInici(),
					filtre.getDataFi());
			organGestors.add(organDto);
			
			for (HistoricExpedientAggregation historic : historics) {
				Date key = historic.getData();
				if (!results.containsKey(key)) {
					results.put(key, new HashMap<OrganGestorDto, HistoricExpedientDto>());
				}
				Map<OrganGestorDto, HistoricExpedientDto> mapOrganGestors = results.get(key);	
				mapOrganGestors.put(
						organDto,
						conversioTipusHelper.convertir(historic, HistoricExpedientDto.class));
			}		
		}
		
		// Fill empty data
		for (Date date : filtre.getQueriedDates()) {
			Map<OrganGestorDto, HistoricExpedientDto> mapOrganGestors = results.get(date);
			if (mapOrganGestors == null) {
				mapOrganGestors = new HashMap<>();
			}
			for (OrganGestorDto organ: organGestors) {
				if (!mapOrganGestors.containsKey(organ)) {
					mapOrganGestors.put(organ, new HistoricExpedientDto(filtre.getTipusAgrupament(), date));
				}
			}
			results.put(date, mapOrganGestors);
			
		}
		return results;
	}

	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(HistoricFiltreDto filtre) {
		Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> mapByDate = this.getDadesOrgansGestors(filtre); 
		Map<OrganGestorDto, List<HistoricExpedientDto>> results = new HashMap<>();
		for (Date data : mapByDate.keySet()) {
			Map<OrganGestorDto, HistoricExpedientDto> dateHistoric = mapByDate.get(data);
			for (OrganGestorDto organ : dateHistoric.keySet()) {
				if (!results.containsKey(organ)) {
					results.put(organ, new ArrayList<HistoricExpedientDto>());
				}
				
				results.get(organ).add(dateHistoric.get(organ));
			}
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
				filtre.getIncorporarExpedientsComuns(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());

		return conversioTipusHelper.convertirList(historic, HistoricUsuariDto.class);
	}

	@Override
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre) {
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricAggregation> historic = historicInteressatRepository.findByDateRangeGroupedByDate(
				interessatDocNum,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());

		return conversioTipusHelper.convertirList(historic, HistoricInteressatDto.class);
	}

	@Transactional
	@Override
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

		Collection<HistoricExpedientEntity> historics = historicTask.calcularHistoricExpedient(
				(new LocalDate()).toDateTimeAtStartOfDay().toDate(),
				(new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricExpedientEntity> resultat = new ArrayList<HistoricExpedientEntity>();
		for (HistoricExpedientEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !fiteringByMetaExpedients ||
					filtre.getMetaExpedientsIds().contains(metaExpedient.getId());
			boolean selectedByOrgan = !fiteringByOrganGestors || (historic.getOrganGestor() != null &&
					filtre.getOrganGestorsIds().contains(historic.getOrganGestor().getId()));
			if (selectedByMetaExp && selectedByOrgan && metaExpedient.getEntitat().equals(entitat)) {
				resultat.add(historic);
			}
		}

		return conversioTipusHelper.convertirList(resultat, HistoricExpedientDto.class);
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
			results.put(conversioTipusHelper.convertir(organGestor, OrganGestorDto.class), sumatori);
		}
		
		if (filtre.getIncorporarExpedientsComuns()) {
			OrganGestorDto organ = new OrganGestorDto();
			organ.setCodi("");
			organ.setNom("Expedients comuns");
			HistoricExpedientDto sumatori = new HistoricExpedientDto(
					null,
					(new LocalDate()).toDateTimeAtCurrentTime().toDate());
			for (HistoricExpedientEntity historic : historics) {
				MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
				boolean selectedByMetaExp = !fiteringByMetaExpedients ||
						filtre.getMetaExpedientsIds().contains(metaExpedient.getId());

				if (selectedByMetaExp && metaExpedient.getOrganGestor() == null) {
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
			results.put(organ, sumatori);
		}

		return results;
	}

	@Transactional
	@Override
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, HistoricFiltreDto filtre) {
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

		Collection<HistoricUsuariEntity> historics = historicTask.calcularHistoricUsuari(
				(new LocalDate()).toDateTimeAtStartOfDay().toDate(),
				(new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricUsuariEntity> resultat = new ArrayList<HistoricUsuariEntity>();
		for (HistoricUsuariEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !fiteringByMetaExpedients ||
					filtre.getMetaExpedientsIds().contains(metaExpedient.getId());
			boolean selectedByOrgan = !fiteringByOrganGestors || (historic.getOrganGestor() != null &&
					filtre.getOrganGestorsIds().contains(historic.getOrganGestor().getId()));
			if (selectedByMetaExp && selectedByOrgan && historic.getUsuari().getCodi().equals(codiUsuari)) {
				resultat.add(historic);
			}
		}
		return conversioTipusHelper.convertirList(resultat, HistoricUsuariDto.class);
	}

	@Transactional
	@Override
	public List<HistoricInteressatDto> getDadesActualsInteressat(String docNum, HistoricFiltreDto filtre) {
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

		Collection<HistoricInteressatEntity> historics = historicTask.calcularHistoricInteressat(
				(new LocalDate()).toDateTimeAtStartOfDay().toDate(),
				(new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricInteressatEntity> resultat = new ArrayList<HistoricInteressatEntity>();
		for (HistoricInteressatEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !fiteringByMetaExpedients ||
					filtre.getMetaExpedientsIds().contains(metaExpedient.getId());
			boolean selectedByOrgan = !fiteringByOrganGestors || (historic.getOrganGestor() != null &&
					filtre.getOrganGestorsIds().contains(historic.getOrganGestor().getId()));
			if (selectedByMetaExp && selectedByOrgan && historic.getInteressatDocNum().equals(docNum)) {
				resultat.add(historic);
			}
		}
		return conversioTipusHelper.convertirList(resultat, HistoricInteressatDto.class);
	}

}
