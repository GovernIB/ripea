package es.caib.ripea.core.aggregation;

import es.caib.ripea.core.entity.MetaExpedientEntity;
import lombok.Data;

@Data
public class MetaExpedientCountAggregation {
	private MetaExpedientEntity metaExpedient;
	private long count;
	
	public MetaExpedientCountAggregation(MetaExpedientEntity metaExpedient, long count) {
		super();
		this.metaExpedient = metaExpedient;
		this.count = count;
	}
	
	
}
