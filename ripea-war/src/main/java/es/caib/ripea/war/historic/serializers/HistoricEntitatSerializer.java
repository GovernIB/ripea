package es.caib.ripea.war.historic.serializers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import es.caib.ripea.war.historic.serializers.HistoricSerializers.DateAdapter;
import es.caib.ripea.war.historic.serializers.HistoricSerializers.RegistreExpedient;
import lombok.Setter;

public class HistoricEntitatSerializer {

	@XmlRootElement(name = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitat implements Serializable {
		public RootEntitat() {}
	}

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
		
	@Setter
	public static class RegistreEntitatDiari extends RegistreExpedient {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
	}
	
	@Setter
	public static class RegistreEntitatMensual extends RegistreExpedient {

		public String mes;
	}
	
	
}
