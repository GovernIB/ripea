package es.caib.ripea.core.aggregation;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricAggregation {

	protected Date data;
	protected Long numExpedientsCreats;
	protected Long numExpedientsCreatsTotal;
	protected Long numExpedientsOberts;
	protected Long numExpedientsObertsTotal;
	protected Long numExpedientsTancats;
	protected Long numExpedientsTancatsTotal;

	public HistoricAggregation(
			Date data,
			Long numExpedientsCreats,
			Long numExpedientsCreatsTotal,
			Long numExpedientsOberts,
			Long numExpedientsObertsTotal,
			Long numExpedientsTancats,
			Long numExpedientsTancatsTotal) {
		this.data = data;
		this.numExpedientsCreats = numExpedientsCreats;
		this.numExpedientsCreatsTotal = numExpedientsCreatsTotal;
		this.numExpedientsOberts = numExpedientsOberts;
		this.numExpedientsObertsTotal = numExpedientsObertsTotal;
		this.numExpedientsTancats = numExpedientsTancats;
		this.numExpedientsTancatsTotal = numExpedientsTancatsTotal;
	}

}
