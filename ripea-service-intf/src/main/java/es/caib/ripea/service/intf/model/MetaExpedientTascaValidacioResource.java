package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaExpedientTascaValidacioResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 1874714893538292162L;
	private ItemValidacioTascaEnum itemValidacio;
    private TipusValidacioTascaEnum tipusValidacio;
	protected Long itemId;
	protected boolean activa = true;
	private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;
}