package es.caib.ripea.service.intf.model;

import java.util.List;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = {"nom"},
		descriptionField = "nom",
		defaultSortFields = { @ResourceConfig.ResourceSort(field = "nom", direction = Sort.Direction.ASC) }
)
public class MetaExpedientCarpetaResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 3018060925163121468L;
	private int version;
	@NotNull protected String nom;
	private ResourceReference<MetaExpedientCarpetaResource, Long> pare;
	private List<ResourceReference<MetaExpedientCarpetaResource, Long>> fills;
    @NotNull private ResourceReference<MetaExpedientResource, Long> metaExpedient;
}