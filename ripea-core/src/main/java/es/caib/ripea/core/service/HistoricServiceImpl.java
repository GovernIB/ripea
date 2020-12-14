package es.caib.ripea.core.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
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
import es.caib.ripea.core.helper.HistoricHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.repository.historic.HistoricExpedientRepository;
import es.caib.ripea.core.repository.historic.HistoricInteressatRepository;
import es.caib.ripea.core.repository.historic.HistoricUsuariRepository;

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
	private HistoricHelper historicHelper;


	@Override
	public void generateOldHistorics() {
		historicHelper.generateOldDailyHistorics(30*8);
		historicHelper.generateOldMontlyHistorics(12*3);
	}
	
	@Override
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
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
		historicEntitat = fillEmptyData(filtre, historicEntitat, HistoricExpedientAggregation.class);
		return conversioTipusHelper.convertirList(historicEntitat, HistoricExpedientDto.class);
	}

	@Override
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(HistoricFiltreDto filtre) {
		Map<OrganGestorDto, List<HistoricExpedientDto>> data = getHistoricsByOrganGestor(filtre);
		
		if (data.keySet().isEmpty()) {
			return null;
		}
		
		// Format data
		List<Date> dates = filtre.getQueriedDates();
		Collections.reverse(dates);
		
		int i = 0;
		Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> results = new HashMap<>();
		for (Date date : dates) {
			Map<OrganGestorDto, HistoricExpedientDto> mapDate = new HashMap<>();
			for (OrganGestorDto organ: data.keySet()) {
				mapDate.put(organ, data.get(organ).get(i));
			}
			i++;
			results.put(date, mapDate);
		}
		return results;
	}

	@Override
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(HistoricFiltreDto filtre) {
		List<Long> organGestorIds = filtre.getOrganGestorsIds();
		if (organGestorIds == null) {
			return new HashMap<>();
		}
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		Map<OrganGestorDto, List<HistoricExpedientDto>> results = new HashMap<>();
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
			historics = fillEmptyData(filtre, historics, HistoricExpedientAggregation.class);
			results.put(organDto, conversioTipusHelper.convertirList(historics, HistoricExpedientDto.class));
			
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
			historics = fillEmptyData(filtre, historics, HistoricExpedientAggregation.class);
			results.put(organDto, conversioTipusHelper.convertirList(historics, HistoricExpedientDto.class));
		}
		
		return results;
	}
	
	@Override
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre) {
		UsuariEntity usuari = usuariRepository.findByCodi(usuariCodi);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricUsuariAggregation> historics = historicUsuariRepository.findByDateRangeGroupedByDate(
				usuari,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		historics = fillEmptyData(filtre, historics, HistoricUsuariAggregation.class);
		for (HistoricUsuariAggregation h: historics) {
			h.setUsuari(usuari);
		}
		return conversioTipusHelper.convertirList(historics, HistoricUsuariDto.class);
	}

	@Override
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre) {
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		List<HistoricAggregation> historics = historicInteressatRepository.findByDateRangeGroupedByDate(
				interessatDocNum,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!fiteringByMetaExpedients,
				!fiteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		historics = fillEmptyData(filtre, historics, HistoricAggregation.class);
		return conversioTipusHelper.convertirList(historics, HistoricInteressatDto.class);
	}

	@Transactional
	@Override
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		boolean fiteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

		Collection<HistoricExpedientEntity> historics = historicHelper.calcularHistoricExpedient(
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
			boolean selectedByExpedientComu = filtre.getIncorporarExpedientsComuns() && 
					historic.getOrganGestor() == null;
			if (selectedByMetaExp && (selectedByOrgan || selectedByExpedientComu) && metaExpedient.getEntitat().equals(entitat)) {
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
		Collection<HistoricExpedientEntity> historics = historicHelper.calcularHistoricExpedient(
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

		Collection<HistoricUsuariEntity> historics = historicHelper.calcularHistoricUsuari(
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

		Collection<HistoricInteressatEntity> historics = historicHelper.calcularHistoricInteressat(
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

	/**
	 * 
	 * @param <T>
	 * @param filtre
	 * @param historics Llista d'històrics, ha d'estar ordenada descendentment per
	 *                  data
	 * @return
	 */
	private <T extends HistoricAggregation> List<T> fillEmptyData(HistoricFiltreDto filtre, List<T> historics, Class<T> cls) {
		List<Date> dates = filtre.getQueriedDates();
		Collections.reverse(dates);
		Iterator<T> it = historics.iterator();
		T currentHistoric = null;
		if (it.hasNext())
			currentHistoric = it.next();
		else
			currentHistoric = emptyInstance(dates.get(0), cls);
		List<T> results = new ArrayList<T>();
		for (Date data : dates) {
			T dateHistoric = null;
			if (data.compareTo(currentHistoric.getData()) < 0) { // anterior que l'actual
				dateHistoric = emptyInstance(data, cls);

			} else if (data.compareTo(currentHistoric.getData()) == 0) { // igual que l'actual
				dateHistoric = currentHistoric;
				if (it.hasNext())
					currentHistoric = it.next();

			} else { // major que l'actual
				dateHistoric = emptyInstance(data, cls);
				
			}
			results.add(dateHistoric);
		}
		return results;
	}

	private <T extends HistoricAggregation> T emptyInstance(Date date, Class<T> cls) {
		try {
			return cls.getConstructor(Date.class).newInstance(date);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
}
