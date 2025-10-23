package es.caib.ripea.service.intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.security.Permission;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "nom"
)
public class AclEntryResource extends BaseResource<Long> {

    private ResourceReference<AclObjIdentityResource, Long> aclObjectIdentity;
    private ResourceReference<AclSidResource, Long> sid;
    private Integer order;
    private ExtendedPermissionEnum mask;
    private Boolean granting;

    private boolean auditSuccess = false;
    private boolean auditFailure = false;
}