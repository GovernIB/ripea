package es.caib.ripea.war.historic.serializers;

import java.util.ArrayList;
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
import es.caib.ripea.war.historic.serializers.HistoricSerializers.Registre;
import lombok.Setter;

public class HistoricUsuariSerializer {


	@XmlRootElement(name = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuaris {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "usuari")
		public List<RegistresUsuari> registres;
		
		public RootUsuaris(List<RegistresUsuari> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootUsuaris() {}
	}
	
	public static class RegistresUsuari {
		@XmlAttribute(name="codi-usuari")
		public String user;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreUsuari> registres;
		
		public RegistresUsuari(String user) {
			super();
			this.user = user;
			this.registres = new ArrayList<>();
		}
		public RegistresUsuari() {}
		
		public void addRegistre(RegistreUsuari registre) {
			this.registres.add(registre);
		}
	}
	
	@Setter
	public static class RegistreUsuari extends Registre {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
		
		public Long numTasquesTramitades;
		public RegistreUsuari() {}
	}
}
