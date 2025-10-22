package es.caib.ripea.service.intf.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusTransicioEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "codiINom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = OrganGestorResource.FILTER_CODE,
                        formClass = OrganGestorResource.FormFilter.class),
        }
)
public class OrganGestorResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";

	private static final long serialVersionUID = 5991380448523763516L;
	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 1000)
	private String nom; // nomCatala
	@Size(max = 1000)
	private String nomEspanyol;
	private boolean actiu;
	@Size(max = 10)
	private String cif;
	private boolean utilitzarCifPinbal;
	private boolean permetreEnviamentPostal;
	private boolean permetreEnviamentPostalDescendents;
	private OrganEstatEnumDto estat;
	private TipusTransicioEnumDto tipusTransicio;
	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> pare;

    public String getCodiINom() {
		return codi + " - " + nom;
	}

    @Getter
    @Setter
    public static class FormFilter implements Serializable {
        private String codi;
        private String nom;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private OrganEstatEnumDto estat;
    }
}