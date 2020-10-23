package es.caib.ripea.war.historic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class DAOHistoric {

	public static RootOrganGestors mapRegistreOrganGestor(Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades) {
		List<RegistresOrganGestor> registres = new ArrayList<>();
		for (Date data : dades.keySet()) {
			List<RegistreOrganGestor> regOrgans = new ArrayList<>();
			for (OrganGestorDto organGestor : dades.get(data).keySet()) {
				HistoricExpedientDto historic = dades.get(data).get(organGestor);
				RegistreOrganGestor registre = new RegistreOrganGestor();
				BeanUtils.copyProperties(historic, registre);
				registre.nomOrganGestor = organGestor.getNom() + " - " + organGestor.getCodi();
				regOrgans.add(registre);
			}
			registres.add(new RegistresOrganGestor(data, regOrgans));
		}
		
		return new RootOrganGestors(registres);		
	}
	
	public static RootUsuaris mapRegistresUsuaris(Map<String, List<HistoricUsuariDto>> dades) {
		List<RegistresUsuari> registres = new ArrayList<>();
		for (String codiUser : dades.keySet()) {
			RegistresUsuari regUser = new  RegistresUsuari(codiUser);
			List<HistoricUsuariDto> listHistorics = dades.get(codiUser);
			for (HistoricUsuariDto historic : listHistorics) {
				RegistreUsuari registre = new RegistreUsuari();
				BeanUtils.copyProperties(historic, registre);	
				regUser.addRegistre(registre);
			}
			
			registres.add(regUser);			
		}
		return new RootUsuaris(registres);
	}
	
	public static RootInteressats mapRegistresInteressats(Map<String, List<HistoricInteressatDto>> dades) {
		List<RegistresInteressat> registres = new ArrayList<>();
		for (String docNum : dades.keySet()) {
			RegistresInteressat regUser = new  RegistresInteressat(docNum);
			List<HistoricInteressatDto> listHistorics = dades.get(docNum);
			for (HistoricInteressatDto historic : listHistorics) {
				RegistreInteressat registre = new RegistreInteressat();
				BeanUtils.copyProperties(historic, registre);	
				regUser.addRegistre(registre);
			}
			
			registres.add(regUser);			
		}
		return new RootInteressats(registres);
	}
	
	public static RootEntitat mapRegistresEntitat(List<HistoricExpedientDto> dades) {
		List<RegistreEntitat> registres = new ArrayList<>();
		for (HistoricExpedientDto historic : dades) {
			RegistreEntitat registre = new RegistreEntitat();
			BeanUtils.copyProperties(historic, registre);	
			registres.add(registre);
		}
			
		return new RootEntitat(registres);
	}
	
	/**********
	 * 
	 * ENTITAT
	 *
	 **********/
	
	@JacksonXmlRootElement(localName = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitat {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		@JacksonXmlElementWrapper(localName = "registres")
		@JacksonXmlProperty(localName = "registre")
		public List<RegistreEntitat> registres;
		
		public RootEntitat(List<RegistreEntitat> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		
		public RootEntitat() {}
		
	}
		
	@Getter @Setter
	public static class RegistreEntitat extends RegistreExpedient {
		@JacksonXmlProperty(isAttribute=true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		private Date data;
	}
	
	/**********
	 * 
	 * ORGANS GESTORS
	 *
	 **********/
	
	@JacksonXmlRootElement(localName = "registres-organGestor")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootOrganGestors {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		@JacksonXmlElementWrapper(localName = "registres")
		@JacksonXmlProperty(localName = "registre")
		public List<RegistresOrganGestor> registres;
		
		public RootOrganGestors(List<RegistresOrganGestor> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		public RootOrganGestors() {}
	}
	
	public static class RegistresOrganGestor {
		@JacksonXmlProperty(isAttribute=true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date data;

		@JacksonXmlProperty(localName = "organ_gestor")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<RegistreOrganGestor> organGestors;
		
		public RegistresOrganGestor(Date data, List<RegistreOrganGestor> organGestors) {
			super();
			this.data = data;
			this.organGestors = organGestors;
		}
		public RegistresOrganGestor() {}
	}

	public static class RegistreOrganGestor extends Registre {
		@JacksonXmlProperty(isAttribute=true, localName = "nom")
		public String nomOrganGestor;
	}
	
	

	/**********
	 * 
	 * USUARIS
	 *
	 **********/
	

	@JacksonXmlRootElement(localName = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuaris {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		
		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@JacksonXmlProperty(localName = "usuari")
		public List<RegistresUsuari> registres;
		
		public RootUsuaris(List<RegistresUsuari> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
	}
	
	public static class RegistresUsuari {
		@JacksonXmlProperty(isAttribute=true, localName="codi-usuari")
		public String user;
		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@JacksonXmlProperty(localName = "registre")
		public List<RegistreUsuari> registres;
		
		public RegistresUsuari(String user) {
			super();
			this.user = user;
			this.registres = new ArrayList<>();
		}
		
		public void addRegistre(RegistreUsuari registre) {
			this.registres.add(registre);
		}
	}
	
	@Getter @Setter
	public static class RegistreUsuari extends Registre {
		@JacksonXmlProperty(isAttribute=true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		private Date data;
		
		private Long numTasquesTramitades;
	}
	


	/**********
	 * 
	 * INTERESSATS
	 *
	 **********/
	
	@JacksonXmlRootElement(localName = "registres-usuaris")
	public static class RootInteressats {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@JacksonXmlProperty(localName = "interessat")
		public List<RegistresInteressat> registres;
		
		public RootInteressats(List<RegistresInteressat> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
	}
	
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistresInteressat {
		@JacksonXmlProperty(isAttribute=true, localName="numero-document")
		public String interessatDocNum;
		@JacksonXmlElementWrapper(localName = "registres", useWrapping = false)
		@JacksonXmlProperty(localName = "registre")
		public List<RegistreInteressat> registres;
		
		public RegistresInteressat(String interessatDocNum) {
			this.interessatDocNum = interessatDocNum;
			this.registres = new ArrayList<>();
		}
		
		public void addRegistre(RegistreInteressat registre) {
			this.registres.add(registre);
		}
	}
	
	@Getter @Setter
	public static class RegistreInteressat extends Registre {
		@JacksonXmlProperty(isAttribute=true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		private Date data;
	}

	/**********
	 * 
	 * GENERALS
	 *
	 **********/
		
	@Data
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistreExpedient extends Registre {
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
	
}
