package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
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
        descriptionField = "nom",
		artifacts = {
            @ResourceConfigArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = DocumentPortafirmesResource.ACTION_CANCEL_FIRMA,
                    formClass = Serializable.class,
                    requiresId = true),
		}
)
public class DocumentPortafirmesResource extends DocumentEnviamentResource {

	private static final long serialVersionUID = -2165194144445671882L;

	public static final String ACTION_CANCEL_FIRMA = "CANCEL_FIRMA";
	
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
