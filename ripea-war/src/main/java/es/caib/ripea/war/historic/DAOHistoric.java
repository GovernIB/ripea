package es.caib.ripea.war.historic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
	
	@XmlRootElement(name = "registres-entitat")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEntitat implements Serializable {
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
//		@XmlElementWrapper(name = "registres")
		@XmlElement(name = "registre")
		public List<RegistreEntitat> registres;
		
		public RootEntitat(List<RegistreEntitat> registres) {
			super();
			this.generationDate = new Date();
			this.registres = registres;
		}
		
		public RootEntitat() {}
		
	}
		
	@Setter
	public static class RegistreEntitat extends RegistreExpedient {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		private Date data;
	}
	
	/**********
	 * 
	 * ORGANS GESTORS
	 *
	 **********/
	
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
	
	

	/**********
	 * 
	 * USUARIS
	 *
	 **********/
	

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
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date data;
		
		public Long numTasquesTramitades;
		public RegistreUsuari() {}
	}
	


	/**********
	 * 
	 * INTERESSATS
	 *
	 **********/
	
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
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date data;
	}

	/**********
	 * 
	 * GENERALS
	 *
	 **********/
		
	@Data
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RegistreExpedient extends Registre  implements Serializable {
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
	
	public static class DateAdapter extends XmlAdapter<String, Date> {

	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	    @Override
	    public String marshal(Date v) throws Exception {
	        synchronized (dateFormat) {
	            return dateFormat.format(v);
	        }
	    }

	    @Override
	    public Date unmarshal(String v) throws Exception {
	        synchronized (dateFormat) {
	            return dateFormat.parse(v);
	        }
	    }

	}
}
