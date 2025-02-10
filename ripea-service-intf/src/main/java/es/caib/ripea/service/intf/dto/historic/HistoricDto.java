package es.caib.ripea.service.intf.dto.historic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Data
public class HistoricDto implements Comparable<HistoricDto> {

	@JsonIgnore
	protected Long entitat;

	@JsonIgnore
	protected MetaExpedientDto metaExpedient;

	@JsonIgnore
	protected HistoricTipusEnumDto tipus;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
	protected Date data;

	protected Long numExpedientsCreats;

	protected Long numExpedientsCreatsTotal;
//	protected Long numExpedientsOberts;
//	protected Long numExpedientsObertsTotal;

	protected Long numExpedientsTancats;

	protected Long numExpedientsTancatsTotal;

	public HistoricDto(HistoricTipusEnumDto tipus, Date data) {
		this();
		this.tipus = tipus;
		this.data = data;
	}

	public HistoricDto() {
		super();
		this.numExpedientsCreats = 0L;
		this.numExpedientsCreatsTotal = 0L;
//		this.numExpedientsOberts = 0L;
//		this.numExpedientsObertsTotal = 0L;
		this.numExpedientsTancats = 0L;
		this.numExpedientsTancatsTotal = 0L;
	}

	public void combinarAmb(HistoricDto historic) {
		this.numExpedientsCreats += historic.getNumExpedientsCreats();
		this.numExpedientsCreatsTotal += historic.getNumExpedientsCreatsTotal();
		this.numExpedientsTancats += historic.getNumExpedientsTancats();
		this.numExpedientsTancatsTotal += historic.getNumExpedientsTancatsTotal();
	}

	@Override
	public int compareTo(HistoricDto o) {
		return this.data.compareTo(o.getData());
	}

	@JsonProperty("metaExpedient")
	public String getMetaExpedientText() {
		if (this.metaExpedient == null) {
			return "";
		}
		return this.metaExpedient.getCodi() + " - " + this.metaExpedient.getNom();
	}

	@JsonProperty("organ_gestor")
	public String getOrganGestor() {
		if (this.metaExpedient == null || this.metaExpedient.getOrganGestor() == null) {
			return "";
		}
		return this.metaExpedient.getOrganGestor().getCodi() + " - " + 
					this.metaExpedient.getOrganGestor().getNom();
	}
	
	public int getMes() {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 return cal.get(Calendar.MONTH) + 1;
	}
	
	public String getAny() {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 return String.valueOf(cal.get(Calendar.YEAR));
	}
	
	public String getMesNomIAny() {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 return getMesNom() + " " + getAny();
	}
	
	public String getMesNom() {

		switch (getMes()) {
		case 1:
			return "Gener";
		case 2:
			return "Febrer";
		case 3:
			return "Març";
		case 4:
			return "Abril";
		case 5:
			return "Maig";
		case 6:
			return "Juny";
		case 7:
			return "Juliol";
		case 8:
			return "Agost";
		case 9:
			return "Setembre";
		case 10:
			return "Octubre";
		case 11:
			return "Novembre";
		case 12:
			return "Desembre";
		default:
			return null;
		}
	}

}
