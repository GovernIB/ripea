package es.caib.ripea.core.helper.historic;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class ExportacioHelper {

	public static int getMes(Date data) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 return cal.get(Calendar.MONTH) + 1;
	}
	
	public static String getMesNom(Date data) {

		switch (getMes(data)) {
		case 1:
			return "Gener";
		case 2:
			return "Febrer";
		case 3:
			return "Març";
		case 4:
			return "Abril";
		case 5:
			return "Maig";
		case 6:
			return "Juny";
		case 7:
			return "Juliol";
		case 8:
			return "Agost";
		case 9:
			return "Setembre";
		case 10:
			return "Octubre";
		case 11:
			return "Novembre";
		case 12:
			return "Desembre";
		default:
			return null;
		}
	}
	
	public static String getAny(Date data) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 return String.valueOf(cal.get(Calendar.YEAR));
	}

}
