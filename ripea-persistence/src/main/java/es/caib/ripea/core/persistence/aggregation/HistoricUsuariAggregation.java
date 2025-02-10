package es.caib.ripea.core.persistence.aggregation;

import es.caib.ripea.core.persistence.entity.UsuariEntity;
import lombok.Data;

import java.util.Date;

@Data
public class HistoricUsuariAggregation extends HistoricAggregation {

	private UsuariEntity usuari;
	private Long numTasquesTramitades;
	
	public HistoricUsuariAggregation(
			Date data,
			Long numExpedientsCreats,
			Long numExpedientsCreatsTotal,
			Long numExpedientsOberts,
			Long numExpedientsObertsTotal,
			Long numExpedientsTancats,
			Long numExpedientsTancatsTotal,
			UsuariEntity usuari,
			Long numTasquesTramitades) {
		super(
				data,
				numExpedientsCreats,
				numExpedientsCreatsTotal,
				numExpedientsOberts,
				numExpedientsObertsTotal,
				numExpedientsTancats,
				numExpedientsTancatsTotal);
		this.usuari = usuari;
		this.numTasquesTramitades = numTasquesTramitades;
	}
	public HistoricUsuariAggregation(
			Date data) {
		super(data);
		this.usuari = null;
		this.numTasquesTramitades = 0L;
	}
}
