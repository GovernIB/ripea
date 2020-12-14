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
//		@XmlJavaTypeAdapter(DateAdapter.class)
//		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//		public Date generationDate;
//		@XmlElementWrapper(name = "registres")
		@XmlElement(name = "registre")
		public List<RegistreEntitat> registres;
				
		public RootEntitat(List<RegistreEntitat> registres) {
			super();
//			this.generationDate = new Date();
			this.registres = registres;
		}
		
		public RootEntitat() {}
		
	}
		
	@Setter
	public static class RegistreEntitat extends RegistreExpedient {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
	}
}
