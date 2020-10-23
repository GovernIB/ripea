package es.caib.ripea.core.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HistoricDto implements Comparable<HistoricDto> {
	
	@JsonIgnore
	protected Long entitat;

	protected MetaExpedientDto metaExpedient;
	
	@JsonIgnore
	protected HistoricTipusEnumDto tipus;

	protected Date data;

	@JsonProperty("EXPEDIENTS_CREATS")
	protected Long numExpedientsCreats;
	
	@JsonProperty("EXPEDIENTS_CREATS_ACUM")
	protected Long numExpedientsCreatsTotal;
//	protected Long numExpedientsOberts;
//	protected Long numExpedientsObertsTotal;
	
	@JsonProperty("EXPEDIENTS_TANCATS")
	protected Long numExpedientsTancats;
	
	@JsonProperty("EXPEDIENTS_TANCATS_ACUM")
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


//	public String formatDate() {
//		return (new SimpleDateFormat("dd-MM-yyyy")).format(data);
//	}

	@Override
	public int compareTo(HistoricDto o) {
		return this.data.compareTo(o.getData());
	}
}
