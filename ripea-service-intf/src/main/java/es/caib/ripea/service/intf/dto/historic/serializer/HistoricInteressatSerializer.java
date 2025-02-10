package es.caib.ripea.service.intf.dto.historic.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricSerializers.DateAdapter;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricSerializers.Registre;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricInteressatSerializer {

	@XmlRootElement(name = "registres-interessats")
	public static class RootInteressats {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;

		public RootInteressats() {}

		@XmlElement(name = "interessat")
		public List<RegistresInteressatDiari> registres;
	}
	
	@XmlRootElement(name = "registres-interessats")
	public static class RootInteressatsDiari extends RootInteressats {

		
		public RootInteressatsDiari(List<RegistresInteressatDiari> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootInteressatsDiari() {}
	}
	
	@XmlRootElement(name = "registres-interessats")
	public static class RootInteressatsMensual extends RootInteressats {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		@XmlElement(name = "interessat")
		public List<RegistresInteressatMensual> registres;
		
		public RootInteressatsMensual(List<RegistresInteressatMensual> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootInteressatsMensual() {}
	}
	
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistresInteressat {

	}
	
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistresInteressatDiari extends RegistresInteressat{
		@XmlAttribute(name="numero-document")
		public String interessatDocNum;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreInteressatDiari> registres;
		
		public RegistresInteressatDiari() {
			super();
		}

		public RegistresInteressatDiari(String interessatDocNum) {
			this.interessatDocNum = interessatDocNum;
			this.registres = new ArrayList<>();
		}
		
		public void addRegistre(RegistreInteressatDiari registre) {
			this.registres.add(registre);
		}
	}
	
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistresInteressatMensual extends RegistresInteressat{
		@XmlAttribute(name="numero-document")
		public String interessatDocNum;
//		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@XmlElement(name = "registre")
		public List<RegistreInteressatMensual> registres;
		
		public RegistresInteressatMensual() {
			super();
		}

		public RegistresInteressatMensual(String interessatDocNum) {
			this.interessatDocNum = interessatDocNum;
			this.registres = new ArrayList<>();
		}
		
		public void addRegistre(RegistreInteressatMensual registre) {
			this.registres.add(registre);
		}
	}
	
	public static class RegistreInteressat extends Registre {
	}
	
	@Setter
	public static class RegistreInteressatDiari extends Registre {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		public Date data;
	}
	
	@Setter
	public static class RegistreInteressatMensual extends Registre {
		public String any;
		public String mes;
	}
}
