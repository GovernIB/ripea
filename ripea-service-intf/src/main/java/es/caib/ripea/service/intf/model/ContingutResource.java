package es.caib.ripea.service.intf.model;

import java.util.Date;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "nom" }, descriptionField = "nom")
public class ContingutResource extends BaseResource<Long> {

	protected String nom;
	protected ContingutTipusEnumDto tipus;
	protected int esborrat = 0;
	protected Date esborratData;
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date arxiuIntentData;
	protected int arxiuReintents;
	protected int ordre;
	protected String numeroRegistre;
	protected boolean arxiuPropagat;
}