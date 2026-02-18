package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaExpedientTascaValidacioResource extends BaseAuditableResource<Long> {
	private static final long serialVersionUID = 1874714893538292162L;
    @ResourceField(onChangeActive = true)
	@NotNull private ItemValidacioTascaEnum itemValidacio = ItemValidacioTascaEnum.DADA;
    @NotNull private TipusValidacioTascaEnum tipusValidacio = TipusValidacioTascaEnum.AP;
	protected Long itemId;
	protected boolean activa = true;
	private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;

    @Transient private ResourceReference<MetaDadaResource, Long> metaDada;
    @Transient private ResourceReference<MetaDocumentResource, Long> metaDocument;
}