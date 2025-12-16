package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.*;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "servei" },
        descriptionField = "servei",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ConsultaPinbalResource.FILTER_CODE,
                        formClass = ConsultaPinbalResource.ConsultaPinbalFormFilter.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ConsultaPinbalResource.PERSPECTIVE_DOCUMENT_CODE),
        }
)
public class ConsultaPinbalResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER_CONSULTA_PINBAL";
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