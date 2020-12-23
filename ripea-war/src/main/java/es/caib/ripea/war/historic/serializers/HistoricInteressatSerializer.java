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

public class HistoricInteressatSerializer {

	@XmlRootElement(name = "registres-interessats")
	public static class RootInteressats {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		@XmlElement(name = "interessat")
		public List<RegistresInteressat> registres;
		
		public RootInteressats(List<RegistresInteressat> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootInteressats() {}
	}
	
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistresInteressat {
		@XmlAttribute(name="numero-document")
		public String interessatDocNum;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreInteressat> registres;
		
		public RegistresInteressat(String interessatDocNum) {
			this.interessatDocNum = interessatDocNum;
			this.registres = new ArrayList<>();
		}
		
		public void addRegistre(RegistreInteressat registre) {
			this.registres.add(registre);
		}
	}
	
	@Setter
	public static class RegistreInteressat extends Registre {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
	}
}
