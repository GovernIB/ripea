package es.caib.ripea.service.helper;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateHelper {
	

	public static Date toStartOfTheDay(Date data) {
		return toDateInicialDia(data);
	}
	public static Date toDateInicialDia(Date data) {
		if (data == null) {
			return null;
		}
		return clearTime(data).getTime();
	}
	
	public static Date toEndOfTheDay(Date data) {
		return toDateFinalDia(data);
	}
	public static Date toDateFinalDia(Date data) {
		if (data == null) {
			return null;
		}
		return endOfDay(data).getTime();
	}
	
	public static Calendar endOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal;
	}
	
	public static Calendar endOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		endOfDay(cal);
		return cal;
	}


	/**
	 * Data límit d'un període de conservació: la data i hora de fa {@code dies} dies naturals. Les dades anteriors a
	 * aquesta data ja no s'han de conservar.
	 *
	 * @throws IllegalArgumentException si dies és inferior a 1: la data límit seria l'hora actual o una de futura i
	 *         s'esborraria tot.
	 */
	public static Date dataLimitConservacio(int dies) {
		if (dies < 1) {
			throw new IllegalArgumentException("El període de conservació ha de ser d'1 dia o més (dies=" + dies + ")");
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -dies);
		return cal.getTime();
	}

	public static Date toStartOfTheMonth(Date data) {
		if (data == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
		clearTime(cal);
		
		return cal.getTime();
	}
	
	
	public static Date toEndOfTheMonth(Date data) {
		if (data == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	
	public static List<Date> getDatesBetween(Date startDate, Date endDate) {
		List<Date> datesInRange = new ArrayList<Date>();
		Calendar calendar = clearTime(startDate);
		Calendar endCalendar = endOfDay(endDate);

		while (calendar.before(endCalendar)) {
			Date result = calendar.getTime();
			datesInRange.add(result);
			calendar.add(Calendar.DATE, 1);
		}

		return datesInRange;
	}

	public static Calendar clearTime(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return clearTime(calendar);
	}
	
	public static Calendar clearTime(Calendar calendar) {
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static String getDayString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(date);
	}
	
	public static String getMonthString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		return sdf.format(date);
	}

	

	
	public static Date getDay(int day, int month, int year) {
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		
		clearTime(calendar);
		
		return calendar.getTime();
	}
	
	public static Date getMonth(int month, int year) {
		Calendar calendar = new GregorianCalendar();
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		
		clearTime(calendar);
		
		return calendar.getTime();
	}
	
	
	

}
