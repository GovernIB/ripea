package es.caib.ripea.service.service;

import es.caib.ripea.persistence.aggregation.HistoricAggregation;
import es.caib.ripea.persistence.aggregation.HistoricExpedientAggregation;
import es.caib.ripea.persistence.aggregation.HistoricUsuariAggregation;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.persistence.repository.historic.HistoricExpedientRepository;
import es.caib.ripea.persistence.repository.historic.HistoricInteressatRepository;
import es.caib.ripea.persistence.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.HistoricHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.historic.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.dto.historic.*;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.HistoricService;
import es.caib.ripea.service.serializers.DAOHistoric;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class HistoricServiceImpl implements HistoricService {

	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private HistoricExpedientRepository historicExpedientRepository;
	@Autowired private HistoricUsuariRepository historicUsuariRepository;
	@Autowired private HistoricInteressatRepository historicInteressatRepository;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private HistoricHelper historicHelper;

	@Autowired private ExportacioCsvHistoric exportacioCsvHistoric;
	@Autowired private ExportacioXMLHistoric exportacioXMLHistoric;

	@Override
	public void generateOldHistorics() {
		historicHelper.generateOldDailyHistorics(30*8);
		historicHelper.generateOldMontlyHistorics(12*3);
	}
	
	@Override
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			String rolActual,
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
			
//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("mes", new String[] { "data" });
		ordenacioMap.put("any", new String[] { "data" });
		Page<HistoricExpedientAggregation> pagina = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!filteringByMetaExpedients,
				!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici() != null ? filtre.getDataInici() : new GregorianCalendar(2000, Calendar.JANUARY, 01).getTime(),
				filtre.getDataFi() != null ? filtre.getDataFi() : (new LocalDate()).toDateTimeAtCurrentTime().toDate(),
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		
		PaginaDto<HistoricExpedientDto> historicEntitatDto = paginacioHelper.toPaginaDto(
				pagina,
				HistoricExpedientDto.class);
		return historicEntitatDto;
	}

	@Override
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;
		
//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		List<HistoricExpedientAggregation> historicEntitat = historicExpedientRepository.findByEntitatAndDateRangeGroupedByDate(
				entitat,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!filteringByMetaExpedients,
				!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici() != null ? filtre.getDataInici() : new GregorianCalendar(2000, Calendar.JANUARY, 01).getTime(),
				filtre.getDataFi() != null ? filtre.getDataFi() : (new LocalDate()).toDateTimeAtCurrentTime().toDate());
		
		//historicEntitat = fillEmptyData(filtre, historicEntitat, HistoricExpedientAggregation.class);
		return conversioTipusHelper.convertirList(historicEntitat, HistoricExpedientDto.class);
	}
  
	@Override
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		Map<OrganGestorDto, List<HistoricExpedientDto>> data = getHistoricsByOrganGestor(entitatId, rolActual, filtre);
		
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
	public List<HistoricOrganGestorSerializer.RegistresOrganGestor> getRegistresDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return DAOHistoric.mapRegistreOrganGestor(getDadesOrgansGestors(entitatId, rolActual, filtre), tipusAgrupament).registres;
	}

	@Override
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			Long entitatId,
			String rolActual, 
			HistoricFiltreDto filtre) {
		List<Long> organGestorIds = filtre.getOrganGestorsIds();
		if (organGestorIds == null) {
			return new HashMap<>();
		}
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;
		
//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Map<OrganGestorDto, List<HistoricExpedientDto>> results = new HashMap<>();
		List<OrganGestorDto> organGestors = new ArrayList<>();
		for (Long organId : organGestorIds) {
			OrganGestorEntity organGestor = organGestorRepository.getOne(organId);
			List<HistoricExpedientAggregation> historics = historicExpedientRepository.findByOrganGestorAndDateRangeGroupedByDate(
					organGestor,
					filtre.getTipusAgrupament(),
					!filteringByMetaExpedients,
					!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
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
					!filteringByMetaExpedients,
					!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
					filtre.getDataInici(),
					filtre.getDataFi());
			organGestors.add(organDto);
			historics = fillEmptyData(filtre, historics, HistoricExpedientAggregation.class);
			results.put(organDto, conversioTipusHelper.convertirList(historics, HistoricExpedientDto.class));
		}
		
		return results;
	}
	
	@Override
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		UsuariEntity usuari = usuariRepository.findByCodi(usuariCodi);
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		List<HistoricUsuariAggregation> historics = historicUsuariRepository.findByDateRangeGroupedByDate(
				usuari,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!filteringByMetaExpedients,
				!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		historics = fillEmptyData(filtre, historics, HistoricUsuariAggregation.class);
		for (HistoricUsuariAggregation h: historics) {
			h.setUsuari(usuari);
		}
		return conversioTipusHelper.convertirList(historics, HistoricUsuariDto.class);
	}

	@Override
	public List<HistoricUsuariSerializer.RegistresUsuariDiari> getRegistresDadesUsuaris(List<String> usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		Map<String, List<HistoricUsuariDto>> results = new HashMap<>();
		for (String codiUsuari : usuarisCodi) {
			results.put(codiUsuari, getDadesUsuari(codiUsuari, null, null, filtre));
		}
		return DAOHistoric.mapRegistresUsuaris(results, tipusAgrupament).registres;
	}

	@Override
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		List<HistoricAggregation> historics = historicInteressatRepository.findByDateRangeGroupedByDate(
				interessatDocNum,
				filtre.getTipusAgrupament(),
				!fiteringByOrganGestors,
				!fiteringByOrganGestors ? null : filtre.getOrganGestorsIds(),
				filtre.getIncorporarExpedientsComuns(),
				!filteringByMetaExpedients,
				!filteringByMetaExpedients ? null : filtre.getMetaExpedientsIds(),
				filtre.getDataInici(),
				filtre.getDataFi());
		historics = fillEmptyData(filtre, historics, HistoricAggregation.class);
		return conversioTipusHelper.convertirList(historics, HistoricInteressatDto.class);
	}

	@Override
	public List<RegistresInteressatDiari> getRegistresDadesInteressat(List<String> interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		Map<String, List<HistoricInteressatDto>> results = new HashMap<>();
		for (String docNum : interessatsDocNum) {
			List<HistoricInteressatDto> historics = getDadesInteressat(docNum, null, null, filtre);
			results.put(docNum, historics);
		}

		return DAOHistoric.mapRegistresInteressats(results, tipusAgrupament).registres;
	}

	@Transactional
	@Override
	public List<HistoricExpedientDto> getDadesActualsEntitat(
			Long entitatId, 
			String rolActual,
			HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Collection<HistoricExpedientEntity> historics = historicHelper.calcularHistoricExpedient(
				new Date(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricExpedientEntity> resultat = new ArrayList<HistoricExpedientEntity>();
		for (HistoricExpedientEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !filteringByMetaExpedients ||
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
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		List<Long> organGestors = filtre.getOrganGestorsIds();
		if (organGestors == null || organGestors.isEmpty()) {
			return null;
		}
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null && filtre.getMetaExpedientsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Collection<HistoricExpedientEntity> historics = historicHelper.calcularHistoricExpedient(
				new Date(),
				HistoricTipusEnumDto.DIARI);
		Map<OrganGestorDto, HistoricExpedientDto> results = new HashMap<>();
		for (Long organId : organGestors) {
			OrganGestorEntity organGestor = organGestorRepository.getOne(organId);
			HistoricExpedientDto sumatori = new HistoricExpedientDto(
					null,
					(new LocalDate()).toDateTimeAtCurrentTime().toDate());
			for (HistoricExpedientEntity historic : historics) {
				MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
				boolean selectedByMetaExp = !filteringByMetaExpedients ||
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
				boolean selectedByMetaExp = !filteringByMetaExpedients ||
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

	@Override
	public List<RegistreOrganGestor> getRegistresDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return DAOHistoric.mapRegistresActualsOrganGestors(getDadesActualsOrgansGestors(entitatId, rolActual, filtre));
	}

	@Transactional
	@Override
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Collection<HistoricUsuariEntity> historics = historicHelper.calcularHistoricUsuari(
				new Date(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricUsuariEntity> resultat = new ArrayList<HistoricUsuariEntity>();
		for (HistoricUsuariEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !filteringByMetaExpedients ||
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
	public List<HistoricInteressatDto> getDadesActualsInteressat(String docNum, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		boolean filteringByMetaExpedients = filtre.getMetaExpedientsIds() != null &&
				filtre.getMetaExpedientsIds().size() > 0;
		boolean fiteringByOrganGestors = filtre.getOrganGestorsIds() != null && filtre.getOrganGestorsIds().size() > 0;

//		### Comprovar permisos consulta estadístiques
		List<Long> metaExpedientsStatisticsAcces = comprovarAccesEstadistiques(entitatId, rolActual);
		if (metaExpedientsStatisticsAcces != null) {
			filtre.setMetaExpedientsIds(metaExpedientsStatisticsAcces);
			filteringByMetaExpedients = true;
		}
		
		Collection<HistoricInteressatEntity> historics = historicHelper.calcularHistoricInteressat(
				new Date(),
				HistoricTipusEnumDto.DIARI);

		List<HistoricInteressatEntity> resultat = new ArrayList<HistoricInteressatEntity>();
		for (HistoricInteressatEntity historic : historics) {
			MetaExpedientEntity metaExpedient = historic.getMetaExpedient();
			boolean selectedByMetaExp = !filteringByMetaExpedients ||
					filtre.getMetaExpedientsIds().contains(metaExpedient.getId());
			boolean selectedByOrgan = !fiteringByOrganGestors || (historic.getOrganGestor() != null &&
					filtre.getOrganGestorsIds().contains(historic.getOrganGestor().getId()));
			if (selectedByMetaExp && selectedByOrgan && historic.getInteressatDocNum().equals(docNum)) {
				resultat.add(historic);
			}
		}
		return conversioTipusHelper.convertirList(resultat, HistoricInteressatDto.class);
	}

	@Override
	public List<Long> comprovarAccesEstadistiques(
			Long entitatId,
			String rolActual) {
		return historicHelper.comprovarAccesEstadistiques(
				entitatId, 
				rolActual);
	}

	@Override
	public FitxerDto exportarHistoricEntitat(EntitatDto entitat, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		List<HistoricExpedientDto> dades = getDadesEntitat(entitat.getId(), rolActual, filtre);

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
			case "json":
				fileContent = (new ExportacioJSONHistoric()).convertDadesEntitat(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicEntitat.json", "application/json", fileContent);
				break;
			case "xlsx":
				fileContent = (new ExportacioExcelEntitatHistoric()).convertDadesEntitat(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicEntitat.xls", "application/vnd.ms-excel", fileContent);
				break;
			case "odf":
				fileContent = (new ExportacioDocHistoric()).convertDadesEntitat(entitat, dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicEntitat.ods", "application/vnd.oasis.opendocument.text", fileContent);
				break;
			case "xml":
				fileContent = exportacioXMLHistoric.convertDadesEntitat(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicEntitat.xml", "application/xml", fileContent);
				break;
			case "csv":
				fileContent = exportacioCsvHistoric.convertDadesEntitat(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicEntitat.csv", "application/csv", fileContent);
				break;
			default:
				throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	@Override
	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		if (format.equals("json")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = getDadesOrgansGestors(entitatId, rolActual, filtre);

			fileContent = (new ExportacioJSONHistoric()).convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.json", "application/json", fileContent);

		} else if (format.equals("xlsx")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = getDadesOrgansGestors(entitatId, rolActual, filtre);
			List<OrganGestorDto> organsGestors = new ArrayList<>();
			for (Long organId : filtre.getOrganGestorsIds()) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findById(organId).orElse(null);
				if (organGestorEntity == null) {
					throw new NotFoundException(organId, OrganGestorEntity.class);
				}
				organsGestors.add(conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class));
			}
			if (filtre.getIncorporarExpedientsComuns()) {
				OrganGestorDto organDto = new OrganGestorDto();
				organDto.setCodi("");
				organDto.setNom("Expedients comuns");
				organsGestors.add(organDto);
			}

			fileContent = (new ExportacioExcelOrganGestorHistoric()).convertDadesOrgansGestors(dades, organsGestors, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.xls", "application/vnd.ms-excel", fileContent);

		} else if (format.equals("odf")) {
			Map<OrganGestorDto, List<HistoricExpedientDto>> dades = getHistoricsByOrganGestor(entitatId, rolActual, filtre);
			fileContent = (new ExportacioDocHistoric()).convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.ods", "application/vnd.oasis.opendocument.text", fileContent);

		} else if (format.equals("xml")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = getDadesOrgansGestors(entitatId, rolActual, filtre);
			fileContent = exportacioXMLHistoric.convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.xml", "application/xml", fileContent);

		} else {
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	@Override
	public FitxerDto exportarHistoricUsuaris(String[] usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		Map<String, List<HistoricUsuariDto>> dades = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			dades.put(codiUsuari, getDadesUsuari(codiUsuari, entitatId, rolActual, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
			case "json":
				fileContent = (new ExportacioJSONHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicUsuaris.json", "application/json", fileContent);
				break;
			case "xlsx":
				fileContent = (new ExportacioExcelUsuariHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicUsuaris.xls", "application/vnd.ms-excel", fileContent);
				break;
			case "odf":
				fileContent = (new ExportacioDocHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicUsuaris.ods", "application/vnd.oasis.opendocument.text", fileContent);
				break;
			case "xml":
				fileContent = exportacioXMLHistoric.convertDadesUsuaris(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicUsuaris.xml", "application/xml", fileContent);
				break;
			default:
				throw new Exception("Unsuported file format");
		}
		return fitxer;
	}

	@Override
	public FitxerDto exportarHistoricInteressats(String[] interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		Map<String, List<HistoricInteressatDto>> dades = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			dades.put(docNum, getDadesInteressat(docNum, entitatId, rolActual, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
			case "json":
				fileContent = (new ExportacioJSONHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicInteressats.json", "application/json", fileContent);
				break;
			case "xlsx":
				fileContent = (new ExportacioExcelInteressatsHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicInteressats.xls", "application/vnd.ms-excel", fileContent);
				break;
			case "odf":
				fileContent = (new ExportacioDocHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicInteressats.ods", "application/vnd.oasis.opendocument.text", fileContent);
				break;
			case "xml":
				fileContent = exportacioXMLHistoric.convertDadesInteressats(dades, filtre.getTipusAgrupament());
				fitxer = new FitxerDto("historicInteressats.xml", "application/xml", fileContent);
				break;
			default:
				throw new Exception("Unsuported file format");
		}

		return fitxer;
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
