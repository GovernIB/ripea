package es.caib.ripea.service.intf.model;

import java.io.Serializable;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "codi", "portafirmesFluxDesc" },
        descriptionField = "portafirmesFluxDesc",
        artifacts = {
        		@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentFluxPortafibResource.ACTION_CREACIO_FLUXE_CODE,
                        formClass = MetaDocumentFluxPortafibResource.UrlFluxForm.class),
        		@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDocumentFluxPortafibResource.ACTION_EDITAR_FLUXE_CODE,
                        requiresId = true),
        })
public class MetaDocumentFluxPortafibResource extends BaseAuditableResource<Long> {

    public static final String ACTION_CREACIO_FLUXE_CODE = "CREACIO_FLUXE";
    public static final String ACTION_EDITAR_FLUXE_CODE = "EDITAR_FLUXE";

    private ResourceReference<MetaDocumentResource, Long> metaDocument;
    private String portafirmesFluxId;
    private String portafirmesFluxDesc;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class UrlFluxForm implements Serializable {
        private String metaDocumentId;
    }
}