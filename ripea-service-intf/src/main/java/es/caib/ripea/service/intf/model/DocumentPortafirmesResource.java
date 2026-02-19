package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
		artifacts = {
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = DocumentPortafirmesResource.ACTION_CANCEL_FIRMA,
                    requiresId = true),
            @ResourceArtifact(
                    type = ResourceArtifactType.FILTER,
                    code = DocumentPortafirmesResource.FILTER_ENVIATS_PORTAFIRMA_CODE,
                    formClass = DocumentPortafirmesResource.EnviatsPortafirmesFilter.class),
		}
)
public class DocumentPortafirmesResource extends DocumentEnviamentResource {

	private static final long serialVersionUID = -2165194144445671882L;

	public static final String ACTION_CANCEL_FIRMA = "CANCEL_FIRMA";
	public static final String FILTER_ENVIATS_PORTAFIRMA_CODE = "FILTER_ENVIATS_PORTAFIRMA";

	private PortafirmesPrioritatEnumDto prioritat;
	private Date caducitatData;
	private String documentTipus;
	@Transient private String documentTipusNom;
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

    @Getter
    @Setter
    public static class EnviatsPortafirmesFilter implements Serializable {
        private String expedient;
        private String document;
        private DocumentEnviamentEstatEnumDto estat;
        private Date dataEnviamentInici;
        private Date dataEnviamentFi;
    }
}
