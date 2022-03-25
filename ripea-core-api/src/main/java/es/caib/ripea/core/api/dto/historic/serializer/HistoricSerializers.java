package es.caib.ripea.core.api.dto.historic.serializer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoricSerializers {

	@SuppressWarnings("serial")
	@Data
	@EqualsAndHashCode(callSuper=false)
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistreExpedient extends Registre  implements Serializable {
		private Long numDocsSignats;
		private Long numDocsNotificats;
	}
	
	@Data
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class Registre {
		private Long numExpedientsCreats;
		private Long numExpedientsCreatsTotal;
//		private Long numExpedientsOberts;
//		private Long numExpedientsObertsTotal;
		private Long numExpedientsTancats;
		private Long numExpedientsTancatsTotal;
	}
	
	public static class DateAdapter extends XmlAdapter<String, Date> {

	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	    @Override
	    public String marshal(Date v) throws Exception {
	        synchronized (dateFormat) {
				if (v != null) {
					return dateFormat.format(v);
				} else {
					return null;
				}
	        }
	    }

	    @Override
	    public Date unmarshal(String v) throws Exception {
	        synchronized (dateFormat) {
	            return dateFormat.parse(v);
	        }
	    }

	}
}
