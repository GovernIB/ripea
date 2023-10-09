package es.caib.ripea.core.api.dto.historic.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.DateAdapter;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.RegistreExpedient;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class HistoricEntitatSerializer {

	@SuppressWarnings("serial")
	@XmlRootElement(name = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitat implements Serializable {
		public RootEntitat() {}
	}

	@SuppressWarnings("serial")
	@XmlRootElement(name = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitatDiari extends RootEntitat implements Serializable{

		@XmlElement(name = "registre")
		public List<RegistreEntitatDiari> registres;
				
		public RootEntitatDiari(List<RegistreEntitatDiari> registres) {
			super();
			this.registres = registres;
		}
		public RootEntitatDiari() {}
		
	}
	
	@SuppressWarnings("serial")
	@XmlRootElement(name = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitatMensual extends RootEntitat implements Serializable{

		@XmlElement(name = "registre")
		public List<RegistreEntitatMensual> registres;
				
		public RootEntitatMensual(List<RegistreEntitatMensual> registres) {
			super();
			this.registres = registres;
		}
		public RootEntitatMensual() {}
	}
		
	@SuppressWarnings("serial")
	@Setter
	public static class RegistreEntitatDiari extends RegistreExpedient {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
	}
	
	@SuppressWarnings("serial")
	@Setter
	public static class RegistreEntitatMensual extends RegistreExpedient {

		public String any;
		public String mes;
	}
	
	
}
