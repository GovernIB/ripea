package es.caib.ripea.core.task;

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
	
	
	
	//At 10:30 every day:
	//@Scheduled(cron = "0 30 10 * * ?")
	

	/**
	 * Tasca que registra l'hostòric cada dia.
	 * 
	 * S'executa cada dia a les 00:00
	 */
	@Transactional
	@Scheduled(cron = "0 0 0 * * ?")
	public void registreDiari() {
		Date previousDay = (new LocalDate()).minusDays(1).toDateTimeAtStartOfDay().toDate();
		historicHelper.computeData(previousDay, HistoricTipusEnumDto.DIARI);
	}

	/**
	 * Tasca que registra l'històric mensual.
	 * 
	 * S'executa el primer dia de cada mes a les 00:00
	 */
	@Transactional
	@Scheduled(cron = "0 0 0 1 * ?")
	public void registreMensual() {
		Date previousMonth = (new LocalDate()).minusDays(1).withDayOfMonth(1).toDateTimeAtStartOfDay().toDate();
		historicHelper.computeData(previousMonth, HistoricTipusEnumDto.MENSUAL);
	}	
}
