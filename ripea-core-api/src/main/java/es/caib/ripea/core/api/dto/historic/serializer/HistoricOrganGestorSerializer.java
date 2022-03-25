package es.caib.ripea.core.api.dto.historic.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.DateAdapter;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.Registre;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;


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
		
		public RootOrganGestors() {}
	}
	
	@XmlRootElement(name = "registres-organGestor")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootOrganGestorsDiari extends RootOrganGestors {

		@XmlElement(name = "registre")
		public List<RegistresOrganGestorDiari> registres;
		
		public RootOrganGestorsDiari(List<RegistresOrganGestorDiari> registres) {
			super();
			super.generationDate = new Date();
			this.registres = registres;
		}
		public RootOrganGestorsDiari() {}
	}
	
	@XmlRootElement(name = "registres-organGestor")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootOrganGestorsMensual extends RootOrganGestors{
		
		@XmlElement(name = "registre")
		public List<RegistresOrganGestorMensual> registres;
		
		public RootOrganGestorsMensual(List<RegistresOrganGestorMensual> registres) {
			super();
			super.generationDate = new Date();
			this.registres = registres;
		}
		public RootOrganGestorsMensual() {}
	}
	
	
	public static class RegistresOrganGestor {

		public RegistresOrganGestor() {}
	}
	
	public static class RegistresOrganGestorDiari extends RegistresOrganGestor{
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;

		@XmlElement(name = "organ_gestor")
		public List<RegistreOrganGestor> organGestors;
		
		public RegistresOrganGestorDiari(Date data, List<RegistreOrganGestor> organGestors) {
			super();
			this.data = data;
			this.organGestors = organGestors;
		}
		public RegistresOrganGestorDiari() {}
	}
	
	public static class RegistresOrganGestorMensual extends RegistresOrganGestor{

		public String mes;

		@XmlElement(name = "organ_gestor")
		public List<RegistreOrganGestor> organGestors;
		
		public RegistresOrganGestorMensual(String mes, List<RegistreOrganGestor> organGestors) {
			super();
			this.mes = mes;
			this.organGestors = organGestors;
		}
		public RegistresOrganGestorMensual() {}
	}

	public static class RegistreOrganGestor extends Registre {
		@XmlAttribute(name = "nom")
		public String nomOrganGestor;
	}
	
}
