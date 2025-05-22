package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaDocumentResource extends MetaNodeResource {

	private MultiplicitatEnumDto multiplicitat;
	private boolean firmaPortafirmesActiva;
	private String portafirmesDocumentTipus;
	private String portafirmesFluxId;
	private String portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	private String firmaPassarelaCustodiaTipus;
	private String plantillaNom;
	private String plantillaContentType;
	private byte[] plantillaContingut;	
	private int ordre;
	private NtiOrigenEnumDto ntiOrigen;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private String ntiTipoDocumental;
	private boolean firmaBiometricaActiva;
	private boolean biometricaLectura;
	private MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private String codiPropi;
	private boolean pinbalActiu;
	
	private ResourceReference<MetaExpedientResource, Long> metaExpedient;
	private ResourceReference<PinbalServeiResource, Long> pinbalServei;
	
	protected String pinbalFinalitat;
	private boolean pinbalUtilitzarCifOrgan;
	private boolean perDefecte;
}