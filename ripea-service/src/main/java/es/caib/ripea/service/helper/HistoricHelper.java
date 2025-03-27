package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.persistence.entity.HistoricEntity;
import es.caib.ripea.persistence.entity.HistoricExpedientEntity;
import es.caib.ripea.persistence.entity.HistoricInteressatEntity;
import es.caib.ripea.persistence.entity.HistoricUsuariEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ContingutLogRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaRepository;
import es.caib.ripea.persistence.repository.historic.HistoricExpedientRepository;
import es.caib.ripea.persistence.repository.historic.HistoricInteressatRepository;
import es.caib.ripea.persistence.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.service.intf.exception.PermissionDeniedStatisticsException;
import es.caib.ripea.service.intf.utils.DateUtil;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HistoricHelper {

	@Autowired private HistoricExpedientRepository historicExpedientRepository;
	@Autowired private HistoricUsuariRepository historicUsuariRepository;
	@Autowired private HistoricInteressatRepository historicInteressatRepository;
	@Autowired private ContingutLogRepository contingutLogRepository;
	@Autowired private ExpedientTascaRepository expedientTascaRepository;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	
	@Transactional
	public void generateOldMontlyHistorics (int nMonths) {
		LocalDate date = (new LocalDate()).minusDays(1);
		for (int i = 1; i <= nMonths; i++) {
			Date currentDateIni = date.withDayOfMonth(1).toDateTimeAtStartOfDay().minusMonths(i).toDate();
			
			computeData(currentDateIni, HistoricTipusEnumDto.MENSUAL);
		}
	}
	
	@Transactional
	public void generateOldDailyHistorics (int nDays) {
		for (int i = 0; i <= nDays; i++) {
			LocalDate date = (new LocalDate()).minusDays(i);
			Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
			
			computeData(currentDateIni, HistoricTipusEnumDto.DIARI);
		}
	}
	

    
    
    public Date getStartDate(Date date, HistoricTipusEnumDto tipus) {
    	Date dateCalculat = null;
		if (tipus == HistoricTipusEnumDto.DIARI) {
			dateCalculat =  DateHelper.toStartOfTheDay(date);
		} else if (tipus == HistoricTipusEnumDto.MENSUAL) {
			dateCalculat =  DateHelper.toStartOfTheMonth(date);
		}
		return dateCalculat;
    }
    
    public Date getEndDate(Date date, HistoricTipusEnumDto tipus) {
    	Date dateCalculat = null;
		if (tipus == HistoricTipusEnumDto.DIARI) {
			dateCalculat =  DateHelper.toEndOfTheDay(date);
		} else if (tipus == HistoricTipusEnumDto.MENSUAL) {
			dateCalculat =  DateHelper.toEndOfTheMonth(date);
		}
		return dateCalculat;
    }
    
	public boolean checkIfHistoricsExist(Date date, HistoricTipusEnumDto tipus) {

		List<HistoricExpedientEntity> historic = historicExpedientRepository.findByDateAndTipus(
				date,
				tipus);
		return !CollectionUtils.isEmpty(historic);
	}

	@Transactional
	public void computeData(Date date, HistoricTipusEnumDto tipus) {
		
		Collection<HistoricExpedientEntity> historicsExpedients = calcularHistoricExpedient(
				date,
				tipus);
		
		// list of metaexpedients with logs counted on given day/month
		historicExpedientRepository.saveAll(historicsExpedients);

		Collection<HistoricUsuariEntity> historicsUsuaris = calcularHistoricUsuari(
				date,
				tipus);
		historicUsuariRepository.saveAll(historicsUsuaris);

		Collection<HistoricInteressatEntity> historicsInteressats = calcularHistoricInteressat(
				date,
				tipus);
		historicInteressatRepository.saveAll(historicsInteressats);
	}

	/**
	 * Calcula l'històric de tots els metaexpedients de la base de dades de dins el rang de dades especificat
	 * per paràmetre.
	 * @param tipusLog Indica si estem agrupant els històrics per mes o per dia.
	 * 
	 * @return Llistat d'històrics dels metaexpedients de la base de dades 
	 */
	public Collection<HistoricExpedientEntity> calcularHistoricExpedient(
			Date date,
			HistoricTipusEnumDto tipusLog) {
		
		Date startDate = getStartDate(date, tipusLog);
		Date endDate = getEndDate(date, tipusLog);
		
		MapHistoricMetaExpedients mapExpedients = new MapHistoricMetaExpedients(startDate, tipusLog);

		// all logs created for all expedients between dates, count of logs grouped by metaxpedient and log tipus 
		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByMetaExpedient(
				DateUtil.getLocalDateTimeFromDate(startDate, true, false),
				DateUtil.getLocalDateTimeFromDate(endDate, false, true));
//		+---------------+------------+-------+
//		| METAEXPEDIENT | TIPUS      | COUNT |
//		+---------------+------------+-------+
//		| 1             | CREACIO    | 10    |
//		+---------------+------------+-------+
//		| 1             | REOBERTURA | 15    |
//		+---------------+------------+-------+
//		| 1             | TANCAMENT  | 20    |
//		+---------------+------------+-------+
//		| 2             | CREACIO    | 30    |
//		+---------------+------------+-------+
//		| 2             | REOBERTURA | 35    |
//		+---------------+------------+-------+
//		| 2             | TANCAMENT  | 40    |
//		+---------------+------------+-------+
		

		
		// tranform data to get have them grouped by metaexpedient
		registreHistoricExpedients(logsCount, mapExpedients);
//		+---------------+------------+--------------+-------------+		+----------+--------------+-------------+-------------------+
//		| METAEXPEDIENT | NUM_CREATS | NUM_OBERTS 	| NUM_TANCATS |		| ENTITAT  | ORGANGESTOR  | DATE        | TIPUS_LOG 		|
//		+---------------+------------+--------------+-------------+		+----------+--------------+-------------+-------------------+
//		| 1             | 10         | 15           | 20          |		| LIM      | A04026960    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+		+----------+--------------+-------------+-------------------+
//		| 2             | 30         | 35           | 40          |		| LIM      | A04027064    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+		+----------+--------------+-------------+-------------------+
		

		// count all logs created until date specified
		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBeforeCreatedDateGroupByMetaExpedient(
				DateUtil.getLocalDateTimeFromDate(endDate));
		registreHistoricExpedientsAcumulats(logsCountAccum, mapExpedients);
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| METAEXPEDIENT | NUM_CREATS | NUM_OBERTS 	| NUM_TANCATS | NUM_CREATS_TOTAL | NUM_OBERTS_TOTAL   | NUM_TANCATS_TOTAL |		| ENTITAT  | ORGANGESTOR  | DATE        | TIPUS_LOG 		|
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| 1             | 10         | 15           | 20          | 100              | 120                | 130               |		| LIM      | A04026960    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| 2             | 30         | 35           | 40          | 200              | 220                | 230               |		| LIM      | A04027064    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
		

		for (HistoricExpedientEntity historic : mapExpedients.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}
		// NUM_OBERTS = NUM_OBERTS + NUM_CREATS
		// NUM_OBERTS_TOTAL = NUM_OBERTS_TOTAL + NUM_CREATS_TOTAL
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| METAEXPEDIENT | NUM_CREATS | NUM_OBERTS 	| NUM_TANCATS | NUM_CREATS_TOTAL | NUM_OBERTS_TOTAL   | NUM_TANCATS_TOTAL |		| ENTITAT  | ORGANGESTOR  | DATE        | TIPUS_LOG 		|
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| 1             | 10         | 25           | 20          | 100              | 220                | 130               |		| LIM      | A04026960    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+
//		| 2             | 30         | 65           | 40          | 200              | 420                | 230               |		| LIM      | A04027064    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+		+----------+--------------+-------------+-------------------+


		
		List<ContingutLogCountAggregation<MetaExpedientEntity>> countAggregation1 = contingutLogRepository.findLogsDocumentBetweenCreatedDateGroupByMetaExpedient(
				DateUtil.getLocalDateTimeFromDate(startDate, true, false),
				DateUtil.getLocalDateTimeFromDate(endDate, false, true));
		
		for (ContingutLogCountAggregation<MetaExpedientEntity> count : countAggregation1) {
			HistoricExpedientEntity historic = mapExpedients.getHistoric(
					count.getMetaExpedient().getId(),
					startDate,
					count.getMetaExpedient());
			switch (count.getTipus()) {
			case DOC_FIRMAT:
				historic.setNumDocsSignats(count.getCount());
				break;
			default:
				break;
			}
		}
		
		List<ContingutLogCountAggregation<MetaExpedientEntity>> countAggregation2 = contingutLogRepository.findLogsNotificacioBetweenCreatedDateGroupByMetaExpedient(
				DateUtil.getLocalDateTimeFromDate(startDate, true, false),
				DateUtil.getLocalDateTimeFromDate(endDate, false, true));

		for (ContingutLogCountAggregation<MetaExpedientEntity> count : countAggregation2) {
			HistoricExpedientEntity historic = mapExpedients.getHistoric(
					count.getMetaExpedient().getId(),
					startDate,
					count.getMetaExpedient());
			switch (count.getTipus()) {
			case NOTIFICACIO_CERTIFICADA:
			case NOTIFICACIO_REBUTJADA:
				historic.setNumDocsNotificats(count.getCount());
				break;
			default:
				break;
			}
		}
		
		
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+------------------+---------------------+		+----------+--------------+-------------+-------------------+
//		| METAEXPEDIENT | NUM_CREATS | NUM_OBERTS   | NUM_TANCATS | NUM_CREATS_TOTAL | NUM_OBERTS_TOTAL   | NUM_TANCATS_TOTAL | NUM_DOCS_FIRMATS | NUM_DOCS_NOTIFICATS |		| ENTITAT  | ORGANGESTOR  | DATE        | TIPUS_LOG 		|
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+------------------+---------------------+		+----------+--------------+-------------+-------------------+
//		| 1             | 10         | 15           | 20          | 100              | 120                | 130               | 3                | 5                   |		| LIM      | A04026960    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+------------------+---------------------+		+----------+--------------+-------------+-------------------+
//		| 2             | 30         | 35           | 40          | 200              | 220                | 230               | 2                | 4                   |		| LIM      | A04027064    | 2022/11/01  | DIARI/MENSUAL     |
//		+---------------+------------+--------------+-------------+------------------+--------------------+-------------------+------------------+---------------------+		+----------+--------------+-------------+-------------------+

		return mapExpedients.getValues();

	}

	public Collection<HistoricUsuariEntity> calcularHistoricUsuari(
			Date date,
			HistoricTipusEnumDto tipusLog) {

		Date startDate = getStartDate(date, tipusLog);
		Date endDate = getEndDate(date, tipusLog);
		
		List<ContingutLogCountAggregation<UsuariEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				DateUtil.getLocalDateTimeFromDate(startDate, true, false),
				DateUtil.getLocalDateTimeFromDate(endDate, false, true));
		MapHistoricUsuaris mapHistorics = new MapHistoricUsuaris(startDate, tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics);

		List<ContingutLogCountAggregation<UsuariEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				DateUtil.getLocalDateTimeFromDate(endDate));
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics);

		for (HistoricUsuariEntity historic : mapHistorics.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

		List<ContingutLogCountAggregation<UsuariEntity>> numTasquesTramitades = expedientTascaRepository.countByResponsableAndEstat(
				new TascaEstatEnumDto[] { TascaEstatEnumDto.FINALITZADA });
		for (ContingutLogCountAggregation<UsuariEntity> count : numTasquesTramitades) {
			HistoricUsuariEntity historic = mapHistorics.getHistoric(count.getItemGrouped(), count.getMetaExpedient());
			historic.setNumTasquesTramitades(count.getCount());
		}

		return mapHistorics.getValues();
	}

	public Collection<HistoricInteressatEntity> calcularHistoricInteressat(
			Date date,
			HistoricTipusEnumDto tipusLog) {
		
		
		Date startDate = getStartDate(date, tipusLog);
		Date endDate = getEndDate(date, tipusLog);

		List<ContingutLogCountAggregation<String>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				DateUtil.getLocalDateTimeFromDate(startDate, true, false),
						DateUtil.getLocalDateTimeFromDate(endDate, false, true));
		MapHistoricInteressat mapHistorics = new MapHistoricInteressat(startDate, tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics);

		List<ContingutLogCountAggregation<String>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				DateUtil.getLocalDateTimeFromDate(endDate));
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics);

		for (HistoricInteressatEntity historic : mapHistorics.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

		return mapHistorics.getValues();
	}
	
	public List<Long> comprovarAccesEstadistiques(
			Long entitatId,
			String rolActual) {
		List<Long> metaExpedientsEstadistica = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isUsuariActualApiGranted = rolActual == null && auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("IPA_API_HIST"));
		boolean isUsuariActualApiNotGranted = rolActual == null && auth != null && !auth.getAuthorities().contains(new SimpleGrantedAuthority("IPA_API_HIST"));
		
//		Intent consulta estadístiques API sense permís 'IPA_API_HIST'
		if (isUsuariActualApiNotGranted)
			throw new PermissionDeniedStatisticsException("L'usuari " + auth.getName() + " no disposa dels permisos necessaris per consultar les estadístiques");
		
//		Consulta estadístiques amb permís 'IPA_API_HIST' (no comprovar permisos sobre meta-expedients)
		if (isUsuariActualApiGranted)
			return metaExpedientsEstadistica;
		
//		Consulta estadístiques amb permís 'tothom' (comprovar permisos sobre meta-expedients)
		if ("tothom".equals(rolActual)) {
			metaExpedientsEstadistica = getMetaExpedientsPermisStatistics(entitatId);
			if (metaExpedientsEstadistica.isEmpty())
				throw new PermissionDeniedStatisticsException("L'usuari " + auth.getName() + " no disposa de cap permís estadística sobre cap tipus d'expedient");
		}
		return metaExpedientsEstadistica;
	}
	
	private List<Long> getMetaExpedientsPermisStatistics(Long entitatId) {
		List<Long> metaExpedientsPermisStatisticsIds = new ArrayList<Long>();
		List<MetaExpedientEntity> metaExpedientsPermisStatistics = metaExpedientHelper.findAmbPermis(
				entitatId,
				ExtendedPermission.STATISTICS,
				true,
				null, 
				false,
				false,
				null, 
				false);
		for (MetaExpedientEntity metaExpedientEntity : metaExpedientsPermisStatistics) {
			metaExpedientsPermisStatisticsIds.add(metaExpedientEntity.getId());
		}
		return metaExpedientsPermisStatisticsIds;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void registreHistoricExpedients(
			List<ContingutLogCountAggregation<T>> logsCount,
			IMapHistoric mapHistorics) {
		for (ContingutLogCountAggregation countObject : logsCount) {
			LogTipusEnumDto tipusLog = countObject.getTipus();

			HistoricEntity historic = mapHistorics.getHistoric(countObject);
			switch (tipusLog) {
			case CREACIO:
				historic.setNumExpedientsCreats(countObject.getCount());
				break;
			case REOBERTURA:
				historic.setNumExpedientsOberts(countObject.getCount());
				break;
			case TANCAMENT:
				historic.setNumExpedientsTancats(countObject.getCount());
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void registreHistoricExpedientsAcumulats(
			List<ContingutLogCountAggregation<T>> logsCountAccum,
			IMapHistoric mapHistorics) {
		for (ContingutLogCountAggregation countObject : logsCountAccum) {
			LogTipusEnumDto tipusLog = countObject.getTipus();
			HistoricEntity historic = mapHistorics.getHistoric(countObject);

			switch (tipusLog) {
			case CREACIO:
				historic.setNumExpedientsCreatsTotal(countObject.getCount());
				break;
			case REOBERTURA:
				historic.setNumExpedientsObertsTotal(countObject.getCount());
				break;
			case TANCAMENT:
				historic.setNumExpedientsTancatsTotal(countObject.getCount());
				break;
			default:
				break;
			}
		}
	}

	private interface IMapHistoric<K, V extends HistoricEntity> {

		public V getHistoric(ContingutLogCountAggregation<K> countLogs);

		public Collection<V> getValues();

	}

	/**
	 * Classe que gestiona tots els registres d'històrics de metaexpedients d'una
	 * fecha concreta
	 * 
	 * @author bgalmes
	 */
	private class MapHistoricMetaExpedients implements IMapHistoric<MetaExpedientEntity, HistoricExpedientEntity> {

		private Map<Long, HistoricExpedientEntity> mapHistorics;
		private HistoricTipusEnumDto tipusLog;
		private Date date;

		public MapHistoricMetaExpedients(Date date, HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			this.date = date;
			mapHistorics = new HashMap<Long, HistoricExpedientEntity>();
		}

		/**
		 * Obté l'històric del metaexpedient indicat per paràmetre
		 */
		public HistoricExpedientEntity getHistoric(ContingutLogCountAggregation<MetaExpedientEntity> countLogs) {
			HistoricExpedientEntity historicExpedient;
			MetaExpedientEntity metaExpedient = countLogs.getItemGrouped();
			Long key = metaExpedient.getId();
			if (!mapHistorics.containsKey(key)) {
				historicExpedient = new HistoricExpedientEntity(this.date, this.tipusLog);
				historicExpedient.setEntitat(metaExpedient.getEntitat());
				historicExpedient.setOrganGestor(metaExpedient.getOrganGestor());
				historicExpedient.setMetaExpedient(metaExpedient);
				mapHistorics.put(key, historicExpedient);

			} else {
				historicExpedient = mapHistorics.get(key);
			}

			return historicExpedient;
		}

		public Collection<HistoricExpedientEntity> getValues() {
			return this.mapHistorics.values();
		}

		public HistoricExpedientEntity getHistoric(Long key, Date data, MetaExpedientEntity metaExpedient) {
			HistoricExpedientEntity historicExpedient;
			if (!mapHistorics.containsKey(key)) {
				historicExpedient = new HistoricExpedientEntity(data, this.tipusLog);
				historicExpedient.setEntitat(metaExpedient.getEntitat());
				historicExpedient.setOrganGestor(metaExpedient.getOrganGestor());
				historicExpedient.setMetaExpedient(metaExpedient);
				mapHistorics.put(key, historicExpedient);

			} else {
				historicExpedient = mapHistorics.get(key);
			}

			return historicExpedient;
		}

	}

	/**
	 * Classe que gestiona tots els registres d'històrics d'usuaris d'una fecha
	 * concreta
	 * 
	 * @author bgalmes
	 */
	private class MapHistoricUsuaris implements IMapHistoric<UsuariEntity, HistoricUsuariEntity> {

		private Map<String, Map<Long, HistoricUsuariEntity>> mapHistorics;
		private HistoricTipusEnumDto tipusLog;
		private Date date;

		public MapHistoricUsuaris(Date date, HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			this.date = date;
			mapHistorics = new HashMap<String, Map<Long, HistoricUsuariEntity>>();
		}

		/**
		 * Obté l'històric de l'usuari indicat per paràmetre
		 */
		public HistoricUsuariEntity getHistoric(ContingutLogCountAggregation<UsuariEntity> countLogs) {
			UsuariEntity usuari = countLogs.getItemGrouped();
			MetaExpedientEntity metaExpedient = countLogs.getMetaExpedient();

			return getHistoric(usuari, metaExpedient);
		}

		public HistoricUsuariEntity getHistoric(UsuariEntity usuari, MetaExpedientEntity metaExpedient) {
			HistoricUsuariEntity historicUsuari;
			Map<Long, HistoricUsuariEntity> historicMetaExpedients;

			if (!mapHistorics.containsKey(usuari.getCodi())) {
				historicUsuari = getEmptyHistoric(usuari, metaExpedient);
				historicMetaExpedients = new HashMap<>();
				historicMetaExpedients.put(metaExpedient.getId(), historicUsuari);
				mapHistorics.put(usuari.getCodi(), historicMetaExpedients);

			} else {
				historicMetaExpedients = mapHistorics.get(usuari.getCodi());
				if (!historicMetaExpedients.containsKey(metaExpedient.getId())) {
					historicUsuari = getEmptyHistoric(usuari, metaExpedient);
					historicMetaExpedients.put(metaExpedient.getId(), historicUsuari);

				} else {
					historicUsuari = historicMetaExpedients.get(metaExpedient.getId());
				}
			}

			return historicUsuari;
		}

		private HistoricUsuariEntity getEmptyHistoric(UsuariEntity usuari, MetaExpedientEntity metaExpedient) {
			HistoricUsuariEntity historicUsuari = new HistoricUsuariEntity(this.date, this.tipusLog);
			historicUsuari.setEntitat(metaExpedient.getEntitat());
			historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
			historicUsuari.setMetaExpedient(metaExpedient);
			historicUsuari.setUsuari(usuari);

			return historicUsuari;
		}

		public Collection<HistoricUsuariEntity> getValues() {
			List<HistoricUsuariEntity> results = new ArrayList<HistoricUsuariEntity>();
			for (Map<Long, HistoricUsuariEntity> historics : this.mapHistorics.values()) {
				results.addAll(historics.values());
			}
			return results;
		}

	}

	/**
	 * Classe que gestiona tots els registres d'històrics d'interessats d'una fecha
	 * concreta
	 * 
	 * @author bgalmes
	 */
	private class MapHistoricInteressat implements IMapHistoric<String, HistoricInteressatEntity> {

		private Map<String, Map<Long, HistoricInteressatEntity>> mapHistorics;
		private HistoricTipusEnumDto tipusLog;
		private Date date;

		public MapHistoricInteressat(Date date, HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			this.date = date;
			mapHistorics = new HashMap<String, Map<Long, HistoricInteressatEntity>>();
		}

		public HistoricInteressatEntity getHistoric(ContingutLogCountAggregation<String> countLogs) {
			String interessat = countLogs.getItemGrouped();
			MetaExpedientEntity metaExpedient = countLogs.getMetaExpedient();

			return this.getHistoric(interessat, metaExpedient);
		}

		public HistoricInteressatEntity getHistoric(String interessat, MetaExpedientEntity metaExpedient) {
			HistoricInteressatEntity historicInteressat;
			Map<Long, HistoricInteressatEntity> historicMetaExpedients;
			if (!mapHistorics.containsKey(interessat)) {
				historicInteressat = getEmptyHistoric(interessat, metaExpedient);
				historicMetaExpedients = new HashMap<>();
				historicMetaExpedients.put(metaExpedient.getId(), historicInteressat);
				mapHistorics.put(interessat, historicMetaExpedients);

			} else {
				historicMetaExpedients = mapHistorics.get(interessat);
				if (!historicMetaExpedients.containsKey(metaExpedient.getId())) {
					historicInteressat = getEmptyHistoric(interessat, metaExpedient);
					historicMetaExpedients.put(metaExpedient.getId(), historicInteressat);

				} else {
					historicInteressat = historicMetaExpedients.get(metaExpedient.getId());
				}
			}
			return historicInteressat;
		}

		private HistoricInteressatEntity getEmptyHistoric(String interessat, MetaExpedientEntity metaExpedient) {
			HistoricInteressatEntity historicUsuari = new HistoricInteressatEntity(this.date, this.tipusLog);
			historicUsuari.setEntitat(metaExpedient.getEntitat());
			historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
			historicUsuari.setMetaExpedient(metaExpedient);
			historicUsuari.setInteressatDocNum(interessat);

			return historicUsuari;
		}

		public Collection<HistoricInteressatEntity> getValues() {
			List<HistoricInteressatEntity> results = new ArrayList<HistoricInteressatEntity>();
			for (Map<Long, HistoricInteressatEntity> historics : this.mapHistorics.values()) {
				results.addAll(historics.values());
			}
			return results;
		}

	}

}
