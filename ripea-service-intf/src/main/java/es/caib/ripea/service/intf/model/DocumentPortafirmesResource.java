package es.caib.ripea.service.intf.model;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom"
)
public class DocumentPortafirmesResource extends DocumentEnviamentResource {

	private PortafirmesPrioritatEnumDto prioritat;
	private Date caducitatData;
	private String documentTipus;
	private String responsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus;
	private MetaDocumentFirmaFluxTipusEnumDto fluxTipus;
	private String fluxId;
	private String portafirmesId;
	private PortafirmesCallbackEstatEnumDto callbackEstat;
	private String motiuRebuig;
	private Boolean avisFirmaParcial;
	private Boolean firmaParcial;
	
	@Transient private String urlFluxSeguiment;
}
