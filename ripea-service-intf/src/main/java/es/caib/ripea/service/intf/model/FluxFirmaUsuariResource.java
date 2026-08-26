package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Recurs per al manteniment dels fluxos de firma d'un usuari.
 *
 * Els fluxos no es creen ni es modifiquen des d'un formulari: es fa des de la interfície
 * de PortaFIB dins d'un iframe. Les accions CREAR_FLUX i EDITAR_FLUX només retornen la url
 * a mostrar dins l'iframe; el flux es persisteix quan PortaFIB retorna a la url de retorn
 * (FluxFirmaUsuariController).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom", "descripcio", "destinataris" },
        descriptionField = "nom",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = FluxFirmaUsuariResource.ACTION_CREAR_FLUX_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = FluxFirmaUsuariResource.ACTION_EDITAR_FLUX_CODE,
                        requiresId = true),
        })
public class FluxFirmaUsuariResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = 4317065244907253143L;

	public static final String ACTION_CREAR_FLUX_CODE = "CREAR_FLUX";
	public static final String ACTION_EDITAR_FLUX_CODE = "EDITAR_FLUX";

	private String nom;
	private String descripcio;
	private String portafirmesFluxId;
	private String destinataris;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<UsuariResource, String> usuari;

}
