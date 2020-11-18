package es.caib.ripea.core.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.aggregation.ContingutLogCountAggregation;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.HistoricEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.HistoricInteressatEntity;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.ContingutLogRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.HistoricExpedientRepository;
import es.caib.ripea.core.repository.HistoricInteressatRepository;
import es.caib.ripea.core.repository.HistoricUsuariRepository;

@Component
public class HistoricTask {

	@Autowired
	private HistoricExpedientRepository historicExpedientRepository;
	@Autowired
	private HistoricUsuariRepository historicUsuariRepository;
	@Autowired
	private HistoricInteressatRepository historicInteressatRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;

	/*
	 * TODO: ajustar expressions cron https://www.baeldung.com/cron-expressions
	 * https://howtodoinjava.com/spring-core/spring-scheduled-annotation/
	 * https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks
	 * 
	 */
	/**
	 * Tasca que registra l'hostòric cada dia.
	 * 
	 * S'executa cada dia a les 00:00
	 */

	@Transactional
	@Scheduled(cron = "0 0 0 * * ?")
	public void registreDiari() {
		// get yesterday day
		LocalDate date = (new LocalDate()).minusDays(1);
		Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
		Date currentDateEnd = date.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).plusMillis(
				999).toDate();

		computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.DIARI);
	}

	/**
	 * Tasca que registra l'històric mensual.
	 * 
	 * S'executa el primer dia de cada mes a les 00:00
	 */

	@Transactional
	@Scheduled(cron = "0 0 0 1 * ?")
	public void registreMensual() {
		LocalDate date = (new LocalDate()).minusDays(1);
		Date currentDateIni = date.withDayOfMonth(1).toDateTimeAtStartOfDay().toDate();
		
		// Get last day of month
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDateIni);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date currentDateEnd = cal.getTime();		
		
		computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.MENSUAL);
	}
	
	@Transactional
	public void generateOldMontlyHistorics () {
		LocalDate date = (new LocalDate()).minusDays(1);
		for (int i = 1; i <= 12*2; i++) {
			Date currentDateIni = date.withDayOfMonth(1).toDateTimeAtStartOfDay().minusMonths(i).toDate();

			// Get last day of month
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDateIni);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date currentDateEnd = cal.getTime();

			computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.MENSUAL);
		}
	}
	
	@Transactional
	public void generateOldDailyHistorics () {
		for (int i = 0; i <= 30*12*2; i++) {
			LocalDate date = (new LocalDate()).minusDays(i);
			Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
			Date currentDateEnd = date.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).plusMillis(
					999).toDate();
	
			computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.DIARI);
		}
	}
	
	private void computeData(Date currentDateIni, Date currentDateEnd, HistoricTipusEnumDto tipus) {
		Collection<HistoricExpedientEntity> historicsExpedients = calcularHistoricExpedient(
				currentDateIni,
				currentDateEnd,
				tipus);
		historicExpedientRepository.save(historicsExpedients);

		Collection<HistoricUsuariEntity> historicsUsuaris = calcularHistoricUsuari(
				currentDateIni,
				currentDateEnd,
				tipus);
		historicUsuariRepository.save(historicsUsuaris);

		Collection<HistoricInteressatEntity> historicsInteressats = calcularHistoricInteressat(
				currentDateIni,
				currentDateEnd,
				tipus);
		historicInteressatRepository.save(historicsInteressats);
	}

	public Collection<HistoricExpedientEntity> calcularHistoricExpedient(
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByMetaExpedient(
				currentDateIni,
				currentDateEnd);
		MapHistoricMetaExpedients mapExpedients = new MapHistoricMetaExpedients(currentDateIni, tipusLog);
		registreHistoricExpedients(logsCount, mapExpedients);

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBeforeCreatedDateGroupByMetaExpedient(
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapExpedients);

		for (HistoricExpedientEntity historic : mapExpedients.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

		List<ContingutLogCountAggregation<MetaExpedientEntity>> countsSignats = contingutLogRepository.findLogsDocumentBetweenCreatedDateGroupByMetaExpedient(
				currentDateIni,
				currentDateEnd);
		for (ContingutLogCountAggregation<MetaExpedientEntity> count : countsSignats) {
			HistoricExpedientEntity historic = mapExpedients.getHistoric(
					count.getMetaExpedient().getId(),
					currentDateIni,
					count.getMetaExpedient());
			switch (count.getTipus()) {
			case DOC_FIRMAT:
				historic.setNumDocsSignats(count.getCount());
				break;
			case NOTIFICACIO_CERTIFICADA:
				historic.setNumDocsNotificats(count.getCount());
				break;
			default:
				break;
			}

		}

		return mapExpedients.getValues();

	}

	public Collection<HistoricUsuariEntity> calcularHistoricUsuari(
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<UsuariEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				currentDateIni,
				currentDateEnd);
		MapHistoricUsuaris mapHistorics = new MapHistoricUsuaris(currentDateIni, tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics);

		List<ContingutLogCountAggregation<UsuariEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				currentDateEnd);
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
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<String>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				currentDateIni,
				currentDateEnd);
		MapHistoricInteressat mapHistorics = new MapHistoricInteressat(currentDateIni, tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics);

		List<ContingutLogCountAggregation<String>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics);

		for (HistoricInteressatEntity historic : mapHistorics.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

		return mapHistorics.getValues();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void registreHistoricExpedients(
			List<ContingutLogCountAggregation<T>> logsCount,
			IMapHistoric mapHistorics) {
		for (ContingutLogCountAggregation countObject : logsCount) {
			LogTipusEnumDto tipusLog = countObject.getTipus();

			HistoricEntity historicUsuari = mapHistorics.getHistoric(countObject);
			switch (tipusLog) {
			case CREACIO:
				historicUsuari.setNumExpedientsCreats(countObject.getCount());
				break;
			case REOBERTURA:
				historicUsuari.setNumExpedientsOberts(countObject.getCount());
				break;
			case TANCAMENT:
				historicUsuari.setNumExpedientsTancats(countObject.getCount());
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
			HistoricEntity historicUsuari = mapHistorics.getHistoric(countObject);

			switch (tipusLog) {
			case CREACIO:
				historicUsuari.setNumExpedientsCreatsTotal(countObject.getCount());
				break;
			case REOBERTURA:
				historicUsuari.setNumExpedientsObertsTotal(countObject.getCount());
				break;
			case TANCAMENT:
				historicUsuari.setNumExpedientsTancatsTotal(countObject.getCount());
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
