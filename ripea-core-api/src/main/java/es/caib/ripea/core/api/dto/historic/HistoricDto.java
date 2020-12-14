package es.caib.ripea.core.api.dto.historic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import lombok.Data;

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

}
