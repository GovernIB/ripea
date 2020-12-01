package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class HistoricFiltreDto {

	private Date dataInici;
	private Date dataFi;

	private List<Long> organGestorsIds;
	private List<Long> metaExpedientsIds;

	private Boolean incorporarExpedientsComuns;
	
	private HistoricDadesMostrarEnum dadesMostrar;
		
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET 
	
	
	public List<Date> getQueriedDates() {		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date dataFinal = cal.getTime();
		
		cal = Calendar.getInstance();
		cal.setTime(dataInici);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date data = cal.getTime();
		

		List<Date> dates = new ArrayList<Date> ();
		dates.add(data);
		while(data.compareTo(dataFinal) < 0) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			data = cal.getTime();
			dates.add(data);
		}
		
		return dates;
	}

}
