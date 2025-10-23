package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "nom"
)
public class AclObjIdentityResource extends BaseResource<Long> {

    private ResourceReference<AclClassResource, Long> classEntity;
    private Long objectId;
    private ResourceReference<AclSidResource, Long> ownerSid;
    private List<ResourceReference<AclEntryResource, Long>> entries;
    private boolean entriesInheriting = true;

}