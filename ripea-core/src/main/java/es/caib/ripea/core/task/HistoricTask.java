package es.caib.ripea.core.task;

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
//		for (int i = 1; i <= 60; i++) {
//			LocalDate date = (new LocalDate()).minusDays(i);
		LocalDate date = (new LocalDate()).minusDays(1);
		Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
		Date currentDateEnd = date.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(
				59).plusMillis(999).toDate();

		Collection<HistoricExpedientEntity> historics = calcularHistoricExpedient(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.DIARI);
		historicExpedientRepository.save(historics);

		Collection<HistoricUsuariEntity> historicsUsuaris = calcularHistoricUsuari(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.DIARI);
		historicUsuariRepository.save(historicsUsuaris);
		
		Collection<HistoricInteressatEntity> historicsInteressats = calcularHistoricInteressat(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.DIARI);
		historicInteressatRepository.save(historicsInteressats);

//		}
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
//		for (int i = 1; i <= 24; i++) {
//			date = date.minusMonths(1);
		Date currentDateEnd = date.withDayOfMonth(28).toDateTimeAtStartOfDay().plusHours(23).plusMinutes(
				59).plusSeconds(59).plusMillis(999).toDate();
		Date currentDateIni = date.withDayOfMonth(1).toDateTimeAtStartOfDay().toDate();

		Collection<HistoricExpedientEntity> historicsExpedients = calcularHistoricExpedient(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.MENSUAL);
		historicExpedientRepository.save(historicsExpedients);

		Collection<HistoricUsuariEntity> historicsUsuaris = calcularHistoricUsuari(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.MENSUAL);
		historicUsuariRepository.save(historicsUsuaris);
		
		Collection<HistoricInteressatEntity> historicsInteressats = calcularHistoricInteressat(
				currentDateIni,
				currentDateEnd,
				HistoricTipusEnumDto.MENSUAL);
		historicInteressatRepository.save(historicsInteressats);
//		}
	}

	public Collection<HistoricExpedientEntity> calcularHistoricExpedient(
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByMetaExpedient(
				currentDateIni,
				currentDateEnd);
		MapHistoricMetaExpedients mapExpedients = new MapHistoricMetaExpedients(tipusLog);
		registreHistoricExpedients(logsCount, mapExpedients, currentDateIni);

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBeforeCreatedDateGroupByMetaExpedient(
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapExpedients, currentDateIni);

		for (HistoricExpedientEntity historic : mapExpedients.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

//		List<MetaExpedientCountAggregation> countsPendentsSignar = documentRepository.countByEstatGroupByMetaExpedient(
//				DocumentEstatEnumDto.FIRMA_PENDENT);
//		for (MetaExpedientCountAggregation count : countsPendentsSignar) {
//			HistoricExpedientEntity historic = mapExpedients.getHistoric(
//					count.getMetaExpedient().getId(),
//					currentDateIni,
//					count.getMetaExpedient());
//			historic.setNumDocsPendentsSignar(count.getCount());
//		}

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

//		List<MetaExpedientCountAggregation> countsPendentsNotificats = documentRepository.countByNotificacioEstatInGroupByMetaExpedient(
//				new DocumentNotificacioEstatEnumDto[] {
//						DocumentNotificacioEstatEnumDto.PENDENT,
//						DocumentNotificacioEstatEnumDto.ENVIADA,
//						DocumentNotificacioEstatEnumDto.REGISTRADA });
//		for (MetaExpedientCountAggregation count : countsPendentsNotificats) {
//			HistoricExpedientEntity historic = mapExpedients.getHistoric(
//					count.getMetaExpedient().getId(),
//					currentDateIni,
//					count.getMetaExpedient());
//			historic.setNumDocsPendentsNotificar(count.getCount());
//		}

//		List<MetaExpedientCountAggregation> countsExpedientsAmbAlertes = expedientRepository.countByAlertesNotEmptyGroupByMetaExpedient();
//		for (MetaExpedientCountAggregation count : countsExpedientsAmbAlertes) {
//			HistoricExpedientEntity historic = mapExpedients.getHistoric(
//					count.getMetaExpedient().getId(),
//					currentDateIni,
//					count.getMetaExpedient());
//			historic.setNumExpedientsAmbAlertes(count.getCount());
//		}

//		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findAll();
//		for (MetaExpedientEntity metaExpedient : metaExpedients) {
//			HistoricExpedientEntity historic = mapExpedients.getHistoric(
//					metaExpedient.getId(),
//					currentDateIni,
//					metaExpedient);
//			long nExpedientsAmbErrorsValidacio = 0;
//			for (ExpedientEntity expedient : expedientRepository.findByMetaExpedient(metaExpedient)) {
//				if (cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty()) {
//					nExpedientsAmbErrorsValidacio += 1;
//				}
//			}
//			historic.setNumExpedientsAmbErrorsValidacio(nExpedientsAmbErrorsValidacio);
//		}

		return mapExpedients.getValues();

	}

	private Collection<HistoricUsuariEntity> calcularHistoricUsuari(
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<UsuariEntity>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				currentDateIni,
				currentDateEnd);
		MapHistoricUsuaris mapHistorics = new MapHistoricUsuaris(tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics, currentDateIni);

		List<ContingutLogCountAggregation<UsuariEntity>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByCreatedByAndTipus(
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics, currentDateIni);

		for (HistoricUsuariEntity historic : mapHistorics.getValues()) {
			historic.setNumExpedientsOberts(historic.getNumExpedientsOberts() + historic.getNumExpedientsCreats());
			historic.setNumExpedientsObertsTotal(
					historic.getNumExpedientsObertsTotal() + historic.getNumExpedientsCreatsTotal());
		}

		List<ContingutLogCountAggregation<UsuariEntity>> numTasquesTramitades = expedientTascaRepository.countByResponsableAndEstat(
				new TascaEstatEnumDto[] { TascaEstatEnumDto.FINALITZADA });
		for (ContingutLogCountAggregation<UsuariEntity> count : numTasquesTramitades) {
			HistoricUsuariEntity historic = mapHistorics.getHistoric(
					count.getItemGrouped(),
					currentDateIni,
					count.getMetaExpedient());
			historic.setNumTasquesTramitades(count.getCount());
		}

		return mapHistorics.getValues();
	}

	private Collection<HistoricInteressatEntity> calcularHistoricInteressat(Date currentDateIni, Date currentDateEnd, HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<String>> logsCount = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				currentDateIni,
				currentDateEnd);
		MapHistoricInteressat mapHistorics = new MapHistoricInteressat(tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics, currentDateIni);

		List<ContingutLogCountAggregation<String>> logsCountAccum = contingutLogRepository.findLogsExpedientBetweenCreatedDateGroupByInteressatAndTipus(
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics, currentDateIni);

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
			IMapHistoric mapHistorics,
			Date currentDateIni) {
		for (ContingutLogCountAggregation countObject : logsCount) {
			LogTipusEnumDto tipusLog = countObject.getTipus();

			HistoricEntity historicUsuari = mapHistorics.getHistoric(countObject, currentDateIni);
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
			IMapHistoric mapHistorics,
			Date currentDateIni) {
		for (ContingutLogCountAggregation countObject : logsCountAccum) {
			LogTipusEnumDto tipusLog = countObject.getTipus();
			HistoricEntity historicUsuari = mapHistorics.getHistoric(countObject, currentDateIni);

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

		public V getHistoric(ContingutLogCountAggregation<K> countLogs, Date data);

		public Collection<V> getValues();

	}

	private class MapHistoricMetaExpedients implements IMapHistoric<MetaExpedientEntity, HistoricExpedientEntity> {

		private Map<Long, HistoricExpedientEntity> mapHistorics;
		private HistoricTipusEnumDto tipusLog;

		public MapHistoricMetaExpedients(HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			mapHistorics = new HashMap<Long, HistoricExpedientEntity>();
		}

		public HistoricExpedientEntity getHistoric(
				ContingutLogCountAggregation<MetaExpedientEntity> countLogs,
				Date data) {
			HistoricExpedientEntity historicExpedient;
			MetaExpedientEntity metaExpedient = countLogs.getItemGrouped();
			Long key = metaExpedient.getId();
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

	private class MapHistoricUsuaris implements IMapHistoric<UsuariEntity, HistoricUsuariEntity> {

		private Map<String, HistoricUsuariEntity> mapHistorics;
		private HistoricTipusEnumDto tipusLog;

		public MapHistoricUsuaris(HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			mapHistorics = new HashMap<String, HistoricUsuariEntity>();
		}

		public HistoricUsuariEntity getHistoric(ContingutLogCountAggregation<UsuariEntity> countLogs, Date data) {
			HistoricUsuariEntity historicUsuari;
			UsuariEntity usuari = countLogs.getItemGrouped();
			MetaExpedientEntity metaExpedient = countLogs.getMetaExpedient();
			if (!mapHistorics.containsKey(usuari.getCodi())) {
				historicUsuari = new HistoricUsuariEntity(data, this.tipusLog);
				historicUsuari.setEntitat(metaExpedient.getEntitat());
				historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
				historicUsuari.setMetaExpedient(metaExpedient);
				historicUsuari.setUsuari(usuari);
				mapHistorics.put(usuari.getCodi(), historicUsuari);

			} else {
				historicUsuari = mapHistorics.get(usuari.getCodi());
			}

			return historicUsuari;
		}

		public HistoricUsuariEntity getHistoric(UsuariEntity usuari, Date data, MetaExpedientEntity metaExpedient) {
			HistoricUsuariEntity historicUsuari;
			if (!mapHistorics.containsKey(usuari.getCodi())) {
				historicUsuari = new HistoricUsuariEntity(data, this.tipusLog);
				historicUsuari.setEntitat(metaExpedient.getEntitat());
				historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
				historicUsuari.setMetaExpedient(metaExpedient);
				historicUsuari.setUsuari(usuari);
				mapHistorics.put(usuari.getCodi(), historicUsuari);

			} else {
				historicUsuari = mapHistorics.get(usuari.getCodi());
			}

			return historicUsuari;
		}

		public Collection<HistoricUsuariEntity> getValues() {
			return this.mapHistorics.values();
		}

	}

	private class MapHistoricInteressat implements IMapHistoric<String, HistoricInteressatEntity> {

		private Map<String, HistoricInteressatEntity> mapHistorics;
		private HistoricTipusEnumDto tipusLog;

		public MapHistoricInteressat(HistoricTipusEnumDto tipusLog) {
			this.tipusLog = tipusLog;
			mapHistorics = new HashMap<String, HistoricInteressatEntity>();
		}

		public HistoricInteressatEntity getHistoric(ContingutLogCountAggregation<String> countLogs, Date data) {
			HistoricInteressatEntity historicUsuari;
			String interessat = countLogs.getItemGrouped();
			MetaExpedientEntity metaExpedient = countLogs.getMetaExpedient();
			if (!mapHistorics.containsKey(interessat)) {
				historicUsuari = new HistoricInteressatEntity(data, this.tipusLog);
				historicUsuari.setEntitat(metaExpedient.getEntitat());
				historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
				historicUsuari.setMetaExpedient(metaExpedient);
				historicUsuari.setInteressatDocNum(interessat);
				mapHistorics.put(interessat, historicUsuari);

			} else {
				historicUsuari = mapHistorics.get(interessat);
			}

			return historicUsuari;
		}

		public HistoricInteressatEntity getHistoric(String interessat, Date data, MetaExpedientEntity metaExpedient) {
			HistoricInteressatEntity historicUsuari;
			if (!mapHistorics.containsKey(interessat)) {
				historicUsuari = new HistoricInteressatEntity(data, this.tipusLog);
				historicUsuari.setEntitat(metaExpedient.getEntitat());
				historicUsuari.setOrganGestor(metaExpedient.getOrganGestor());
				historicUsuari.setMetaExpedient(metaExpedient);
				historicUsuari.setInteressatDocNum(interessat);
				mapHistorics.put(interessat, historicUsuari);

			} else {
				historicUsuari = mapHistorics.get(interessat);
			}

			return historicUsuari;
		}

		public Collection<HistoricInteressatEntity> getValues() {
			return this.mapHistorics.values();
		}

	}
}
