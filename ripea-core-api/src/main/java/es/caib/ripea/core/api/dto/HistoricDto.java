package es.caib.ripea.core.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricDto extends AuditoriaDto {
	protected Long entitatId;
	protected Long organGestorId;
	protected Long metaExpedientId;
	protected HistoricTipusEnumDto tipus;
	protected Date data;

	protected Long numExpedientsCreats;
	protected Long numExpedientsCreatsTotal;
//	protected Long numExpedientsOberts;
//	protected Long numExpedientsObertsTotal;
	protected Long numExpedientsTancats;
	protected Long numExpedientsTancatsTotal;
	
	
	public HistoricDto(HistoricTipusEnumDto tipus, Date data) {
		super();
		this.tipus = tipus;
		this.data = data;
				
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

	public HistoricDto() {
		super();
	}
	
}
