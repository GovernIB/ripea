package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "servei" },
        descriptionField = "servei",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ConsultaPinbalResource.FILTER_CODE,
                        formClass = ConsultaPinbalResource.ConsultaPinbalFormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = ConsultaPinbalResource.PERSPECTIVE_AUDIT_CODE),                
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ConsultaPinbalResource.PERSPECTIVE_DOCUMENT_CODE),
        }
)
public class ConsultaPinbalResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER_CONSULTA_PINBAL";
    public static final String PERSPECTIVE_AUDIT_CODE = "AUDITORIA";
    public static final String PERSPECTIVE_DOCUMENT_CODE = "DOCUMENT";

    private ConsultaPinbalEstatEnumDto estat;
    private String pinbalIdpeticion;
    private String error;

    private ResourceReference<PinbalServeiResource, Long> servei;

    private ResourceReference<ExpedientResource, Long> expedient;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    private ResourceReference<DocumentResource, Long> document;

    @Transient DocumentResource documentInfo;

    @Getter
    @Setter
    @FieldNameConstants
    public static class ConsultaPinbalFormFilter implements Serializable {
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<MetaExpedientResource, Long> metaExpedient;
        private ResourceReference<PinbalServeiResource, Long> servei;
        private ResourceReference<UsuariResource, String> createdBy;
        private Date createdDateInici;
        private Date createdDateFi;
        private ConsultaPinbalEstatEnumDto estat;
    }
}