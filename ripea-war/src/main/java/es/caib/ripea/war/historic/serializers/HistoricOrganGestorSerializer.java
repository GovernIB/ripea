package es.caib.ripea.war.historic.serializers;

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


public class HistoricOrganGestorSerializer {

	@XmlRootElement(name = "registres-organGestor")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootOrganGestors {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		
		@XmlElement(name = "registre")
		public List<RegistresOrganGestor> registres;
		
		public RootOrganGestors(List<RegistresOrganGestor> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootOrganGestors() {}
	}
	
	public static class RegistresOrganGestor {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date data;

		@XmlElement(name = "organ_gestor")
		public List<RegistreOrganGestor> organGestors;
		
		public RegistresOrganGestor(Date data, List<RegistreOrganGestor> organGestors) {
			super();
			this.data = data;
			this.organGestors = organGestors;
		}
		public RegistresOrganGestor() {}
	}

	public static class RegistreOrganGestor extends Registre {
		@XmlAttribute(name = "nom")
		public String nomOrganGestor;
	}
	
}
