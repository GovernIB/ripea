package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "nom" }, descriptionField = "nom")
public class ContingutResource extends BaseAuditableResource<Long> {

	@NotNull
	@Size(max = 1024)
	protected String nom;
//	@NotNull
	protected ContingutTipusEnumDto tipus;
	protected int esborrat = 0;
	protected Date esborratData;
	@Size(max = 36)
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date arxiuIntentData;
	protected int arxiuReintents;
	protected int ordre;
	@Size(max = 80)
	protected String numeroRegistre;
	protected boolean arxiuPropagat;

    @Transient
    private boolean conteDocumentsDefinitius;
	
//	@NotNull
	protected ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<ExpedientResource, Long> expedient;
	protected ResourceReference<ContingutResource, Long> pare;
}