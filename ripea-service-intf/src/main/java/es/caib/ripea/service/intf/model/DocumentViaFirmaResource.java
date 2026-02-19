package es.caib.ripea.service.intf.model;

import java.io.Serializable;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.ViaFirmaTipusDestinatariEnum;
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
				@ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = DocumentViaFirmaResource.ACTION_CANCEL_FIRMA,
                    formClass = Serializable.class,
                    requiresId = true),
		}
)
public class DocumentViaFirmaResource extends DocumentEnviamentResource {

	private static final long serialVersionUID = 7803973899943616909L;
	
	public static final String ACTION_CANCEL_FIRMA = "CANCEL_FIRMA";
	
	String codiUsuari;
	String contrasenyaUsuariViaFirma;
	String titol;
	String descripcio;
	String codiDispositiu;
	boolean lecturaObligatoria;
	private String messageCode;
	private ViaFirmaCallbackEstatEnumDto callbackEstat;
	private ViaFirmaTipusDestinatariEnum tipusDestinatari;
	private String signantNif;
	private String signantNom;
	private String signantEmail;
	private String observacions;
	private Boolean validateCodeEnabled;
	private String validateCode;
}
