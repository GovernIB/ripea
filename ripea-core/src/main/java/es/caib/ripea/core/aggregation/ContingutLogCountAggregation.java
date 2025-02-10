package es.caib.ripea.core.aggregation;

import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.persistence.MetaExpedientEntity;
import lombok.Data;

@Data
public class ContingutLogCountAggregation<E> {

	private E itemGrouped;
	private MetaExpedientEntity metaExpedient;
	private LogTipusEnumDto tipus;
	private Long count;

	public ContingutLogCountAggregation(
			E itemGrouped,
			MetaExpedientEntity metaExpedient,
			LogTipusEnumDto tipus,
			Long count) {
		super();
		this.itemGrouped = itemGrouped;
		this.metaExpedient = metaExpedient;
		this.tipus = tipus;
		this.count = count;
	}
	public ContingutLogCountAggregation(
			E itemGrouped,
			MetaExpedientEntity metaExpedient,
			Long count) {
		super();
		this.itemGrouped = itemGrouped;
		this.metaExpedient = metaExpedient;
		this.count = count;
	}
}
