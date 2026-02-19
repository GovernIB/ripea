package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = AclObjIdentityResource.PERSPECTIVE_SID_CODE),
        }
)
public class AclObjIdentityResource extends BaseResource<Long> {

    public static final String PERSPECTIVE_SID_CODE = "SID";

    private ResourceReference<AclClassResource, Long> classEntity;
    private Long objectId;
    private ResourceReference<AclSidResource, Long> ownerSid;
    private List<ResourceReference<AclEntryResource, Long>> entries;
    private boolean entriesInheriting = true;

    @Transient private ResourceReference<OrganGestorResource, Long> organGestor;
    @Transient private List<AclSidResource> sids = new ArrayList<>();
}