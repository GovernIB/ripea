package es.caib.ripea.service.intf.model;

import java.util.List;

import org.springframework.data.domain.Sort;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = {"nom"},
		descriptionField = "nom",
		defaultSortFields = { @ResourceConfig.ResourceSort(field = "nom", direction = Sort.Direction.ASC) }
)
public class MetaExpedientCarpetaResource extends MetaNodeResource {
	private static final long serialVersionUID = 3018060925163121468L;
	private int version;
	protected String nom;
	private ResourceReference<MetaExpedientCarpetaResource, Long> pare;
	private List<ResourceReference<MetaExpedientCarpetaResource, Long>> fills;
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
}