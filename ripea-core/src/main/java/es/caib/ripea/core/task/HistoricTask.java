package es.caib.ripea.core.task;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.helper.HistoricHelper;

@Component
public class HistoricTask {

	@Autowired
	private HistoricHelper historicHelper;
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

		historicHelper.computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.DIARI);
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
		
		historicHelper.computeData(currentDateIni, currentDateEnd, HistoricTipusEnumDto.MENSUAL);
	}	
}
