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
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.HistoricEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.repository.ContingutLogRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.HistoricExpedientRepository;
import es.caib.ripea.core.repository.HistoricUsuariRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.UsuariRepository;

@Component
public class HistoricTask {

	@Autowired
	private HistoricExpedientRepository historicExpedientRepository;
	@Autowired
	private HistoricUsuariRepository historicUsuariRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private CacheHelper cacheHelper;

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
		for (int i = 1; i <= 5; i++) {
			LocalDate date = (new LocalDate()).minusDays(i);
//		LocalDate date = (new LocalDate()).minusDays(1);
		Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
		Date currentDateEnd = date.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).plusMillis(
				999).toDate();

		registreHistoricExpedient(currentDateIni, currentDateEnd, HistoricTipusEnumDto.DIARI);
		registreHistoricUsuari(currentDateIni, currentDateEnd, HistoricTipusEnumDto.DIARI);

		// Historic expedient
//		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findAll();
//		for (MetaExpedientEntity metaExpedient : metaExpedients) {

		// registreHistoricInteressat(metaExpedient, currentDateIni, currentDateEnd);
//		}
		}
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
		for (int i = 1; i <=5; i ++) {
			date = date.minusMonths(1);
			Date currentDateEnd = date.withDayOfMonth(28).toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).plusMillis(
					999).toDate();
			Date currentDateIni = date.withDayOfMonth(1).toDateTimeAtStartOfDay().toDate();
	
			registreHistoricExpedient(currentDateIni, currentDateEnd, HistoricTipusEnumDto.MENSUAL);
			registreHistoricUsuari(currentDateIni, currentDateEnd, HistoricTipusEnumDto.MENSUAL);
		}
	}

	private void registreHistoricExpedient(Date currentDateIni, Date currentDateEnd, HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCount = contingutLogRepository.findLogsBetweenCreatedDateGroupByMetaExpedient(
				LogObjecteTipusEnumDto.EXPEDIENT,
				currentDateIni,
				currentDateEnd);
		MapHistoricMetaExpedients mapExpedients = new MapHistoricMetaExpedients(tipusLog);
		registreHistoricExpedients(logsCount, mapExpedients, currentDateIni);

		List<ContingutLogCountAggregation<MetaExpedientEntity>> logsCountAccum = contingutLogRepository.findLogsBeforeCreatedDateGroupByMetaExpedient(
				LogObjecteTipusEnumDto.EXPEDIENT,
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapExpedients, currentDateIni);

		for (HistoricExpedientEntity histUsuari : mapExpedients.getValues()) {
			histUsuari.setNumExpedientsOberts(
					histUsuari.getNumExpedientsOberts() + histUsuari.getNumExpedientsCreats());
			histUsuari.setNumExpedientsObertsTotal(
					histUsuari.getNumExpedientsObertsTotal() + histUsuari.getNumExpedientsCreatsTotal());
		}

		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findAll();
		for (MetaExpedientEntity metaExpedient : metaExpedients) {
			HistoricExpedientEntity historic = mapExpedients.getHistoric(
					metaExpedient.getId(),
					currentDateIni,
					metaExpedient);
			long nExpedientsAmbErrorsValidacio = 0;
			long nDocsPendentsSignar = 0;
			long nDocsSignats = 0;
			long nDocsNotificats = 0;
			long nDocsPendentsNotificar = 0;
			for (ExpedientEntity expedient : expedientRepository.findByMetaExpedient(metaExpedient)) {
//				if (cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty()) {
//					nExpedientsAmbErrorsValidacio += 1;
//				}

				nDocsPendentsSignar += documentRepository.countByExpedientAndEstat(
						expedient,
						DocumentEstatEnumDto.FIRMA_PENDENT);
				nDocsSignats += documentRepository.countByExpedientAndEstat(expedient, DocumentEstatEnumDto.FIRMAT);
				nDocsNotificats += documentRepository.countByExpedientAndNotificacionsNotificacioEstatIn(
						expedient,
						new DocumentNotificacioEstatEnumDto[] {
								DocumentNotificacioEstatEnumDto.FINALITZADA,
								DocumentNotificacioEstatEnumDto.PROCESSADA });

				nDocsPendentsNotificar += documentRepository.countByExpedientAndNotificacionsNotificacioEstatIn(
						expedient,
						new DocumentNotificacioEstatEnumDto[] {
								DocumentNotificacioEstatEnumDto.PENDENT,
								DocumentNotificacioEstatEnumDto.ENVIADA,
								DocumentNotificacioEstatEnumDto.REGISTRADA });
			}

			historic.setNumExpedientsAmbAlertes(
					Long.valueOf(expedientRepository.findByMetaExpedientAndAlertesNotEmpty(metaExpedient)));
			historic.setNumExpedientsAmbErrorsValidacio(nExpedientsAmbErrorsValidacio);
			historic.setNumDocsPendentsSignar(nDocsPendentsSignar);
			historic.setNumDocsSignats(nDocsSignats);
			historic.setNumDocsPendentsNotificar(nDocsPendentsNotificar);
			historic.setNumDocsNotificats(nDocsNotificats);
		}
		historicExpedientRepository.save(mapExpedients.getValues());

	}

	private void registreHistoricUsuari(Date currentDateIni, Date currentDateEnd, HistoricTipusEnumDto tipusLog) {

		List<ContingutLogCountAggregation<UsuariEntity>> logsCount = contingutLogRepository.findLogsBetweenCreatedDateGroupByCreatedByAndTipus(
				LogObjecteTipusEnumDto.EXPEDIENT,
				currentDateIni,
				currentDateEnd);
		MapHistoricUsuaris mapHistorics = new MapHistoricUsuaris(tipusLog);
		registreHistoricExpedients(logsCount, mapHistorics, currentDateIni);

		List<ContingutLogCountAggregation<UsuariEntity>> logsCountAccum = contingutLogRepository.findLogsBetweenCreatedDateGroupByCreatedByAndTipus(
				LogObjecteTipusEnumDto.EXPEDIENT,
				currentDateEnd);
		registreHistoricExpedientsAcumulats(logsCountAccum, mapHistorics, currentDateIni);

		List<UsuariEntity> usuaris = usuariRepository.findAll();
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findAll();
		for (MetaExpedientEntity metaExpedient : metaExpedients) {
			for (UsuariEntity usuari : usuaris) {
				HistoricUsuariEntity historicUsuari = mapHistorics.getHistoric(usuari, currentDateIni, metaExpedient);
				long numTasquesTramitades = expedientTascaRepository.countByResponsableAndEstat(
						usuari,
						metaExpedient,
						new TascaEstatEnumDto[] { TascaEstatEnumDto.FINALITZADA });

				historicUsuari.setNumTasquesTramitades(numTasquesTramitades);
			}

			for (HistoricUsuariEntity histUsuari : mapHistorics.getValues()) {
				histUsuari.setNumExpedientsOberts(
						histUsuari.getNumExpedientsOberts() + histUsuari.getNumExpedientsCreats());
				histUsuari.setNumExpedientsObertsTotal(
						histUsuari.getNumExpedientsObertsTotal() + histUsuari.getNumExpedientsCreatsTotal());
			}
		}
		historicUsuariRepository.save(mapHistorics.getValues());

	}

//	private void registreHistoricInteressat(
//			MetaExpedientEntity metaExpedient,
//			Date currentDateIni,
//			Date currentDateEnd) {
//		List<String> docNumbers = interessatRepository.findAllDocumentNumbers(metaExpedient);
//		for (String docNum : docNumbers) {
//			HistoricInteressatEntity historicInteressat = new HistoricInteressatEntity(
//					currentDateIni,
//					HistoricTipusEnumDto.DIARI);
//			historicInteressat.setEntitat(metaExpedient.getEntitat());
//			historicInteressat.setOrganGestor(metaExpedient.getOrganGestor());
//			historicInteressat.setMetaExpedient(metaExpedient);
//			historicInteressat.setInteressatDocNum(docNum);
//			int nExpedientsCreatsAvui = contingutLogRepository.findLogsExpedientByInteressatAndBetweenCreatedDate(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.CREACIO,
//					metaExpedient,
//					docNum,
//					currentDateIni,
//					currentDateEnd).size();
//			int nExpedientsCreatsFinsAvui = contingutLogRepository.findLogsExpedientByInteressatAndCreateDateBefore(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.CREACIO,
//					metaExpedient,
//					docNum,
//					currentDateIni).size();
//
//			int nExpedientsObertsAvui = contingutLogRepository.findLogsExpedientByInteressatAndBetweenCreatedDate(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.REOBERTURA,
//					metaExpedient,
//					docNum,
//					currentDateIni,
//					currentDateEnd).size() + nExpedientsCreatsAvui;
//			int nExpedientsObertsTotal = contingutLogRepository.findLogsExpedientByInteressatAndCreateDateBefore(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.REOBERTURA,
//					metaExpedient,
//					docNum,
//					currentDateIni).size() + nExpedientsCreatsFinsAvui;
//
//			int nExpedientsTancatsAvui = contingutLogRepository.findLogsExpedientByInteressatAndBetweenCreatedDate(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.TANCAMENT,
//					metaExpedient,
//					docNum,
//					currentDateIni,
//					currentDateEnd).size();
//			int nExpedientsTancatsTotal = contingutLogRepository.findLogsExpedientByInteressatAndCreateDateBefore(
//					LogObjecteTipusEnumDto.EXPEDIENT,
//					LogTipusEnumDto.TANCAMENT,
//					metaExpedient,
//					docNum,
//					currentDateIni).size();
//			historicInteressat.setNumExpedientsCreats(Long.valueOf(nExpedientsCreatsAvui));
//			historicInteressat.setNumExpedientsCreatsTotal(Long.valueOf(nExpedientsCreatsFinsAvui));
//			historicInteressat.setNumExpedientsOberts(Long.valueOf(nExpedientsObertsAvui));
//			historicInteressat.setNumExpedientsObertsTotal(Long.valueOf(nExpedientsObertsTotal));
//			historicInteressat.setNumExpedientsTancats(Long.valueOf(nExpedientsTancatsAvui));
//			historicInteressat.setNumExpedientsTancatsTotal(Long.valueOf(nExpedientsTancatsTotal));
//		}
//		
//	}
//		

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

}
