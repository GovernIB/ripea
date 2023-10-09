package es.caib.ripea.core.api.dto.historic.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.DateAdapter;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricSerializers.Registre;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricUsuariSerializer {


	@XmlRootElement(name = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuaris {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;

		public RootUsuaris() {}
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "usuari")
		public List<RegistresUsuariDiari> registres;
	}
	
	
	@XmlRootElement(name = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuarisDiari extends RootUsuaris {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		

		
		public RootUsuarisDiari(List<RegistresUsuariDiari> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootUsuarisDiari() {}
	}
	
	@XmlRootElement(name = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuarisMensual extends RootUsuaris{
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "usuari")
		public List<RegistresUsuariMensual> registres;
		
		public RootUsuarisMensual(List<RegistresUsuariMensual> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootUsuarisMensual() {}
	}
	
	
	
	public static class RegistresUsuari {
	}
	
	public static class RegistresUsuariDiari extends RegistresUsuari{
		@XmlAttribute(name="codi-usuari")
		public String user;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreUsuariDiari> registres;
		
		public RegistresUsuariDiari(String user) {
			super();
			this.user = user;
			this.registres = new ArrayList<>();
		}
		public RegistresUsuariDiari() {}
		
		public void addRegistre(RegistreUsuariDiari registre) {
			this.registres.add(registre);
		}
	}
	
	public static class RegistresUsuariMensual extends RegistresUsuari {
		@XmlAttribute(name="codi-usuari")
		public String user;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreUsuariMensual> registres;
		
		public RegistresUsuariMensual(String user) {
			super();
			this.user = user;
			this.registres = new ArrayList<>();
		}
		public RegistresUsuariMensual() {}
		
		public void addRegistre(RegistreUsuariMensual registre) {
			this.registres.add(registre);
		}
	}
	
	@Setter
	public static class RegistreUsuari extends Registre {
	}
	
	@Setter
	public static class RegistreUsuariDiari extends RegistreUsuari {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
		
		public Long numTasquesTramitades;
		public RegistreUsuariDiari() {}
	}
	
	@Setter
	public static class RegistreUsuariMensual extends RegistreUsuari {

		public Long numTasquesTramitades;
		
		public String any;
		public String mes;

		public RegistreUsuariMensual() {}
	}
}
