package es.caib.ripea.service.intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "principal", "sid" },
        descriptionField = "sid",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = AclSidResource.PERSPECTIVE_PERMISION_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AclSidResource.ACTION_MODIFY_PERMISION_CODE,
                        formClass = AclSidResource.ModifyPermisionFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AclSidResource.ACTION_DELETE_PERMISION_CODE,
                        formClass = AclSidResource.DeletePermisionFormAction.class,
                        requiresId = true),
        }
)
public class AclSidResource extends BaseResource<Long> {

    public static final String PERSPECTIVE_PERMISION_CODE = "PERMISION";
    public static final String ACTION_MODIFY_PERMISION_CODE = "MODIFY_PERMISION";
    public static final String ACTION_DELETE_PERMISION_CODE = "DELETE_PERMISION";

    private PrincipalTipusEnumDto principal;
    private String sid;

    @Transient private boolean admin;
    @Transient private boolean adminLectura;
    @Transient private boolean user;

    @JsonIgnore @Transient private List<ExtendedPermissionEnum> masks;
    private List<ResourceReference<AclEntryResource, Long>> entries = new ArrayList<>();

    @Getter
    @Setter
    public static class ModifyPermisionFormAction implements Serializable {
        @NotNull
        private PrincipalTipusEnumDto principal;
        @NotNull
        private String sid;
        private boolean admin;
        private boolean adminLectura;
        private boolean user;
        @NotNull
        private ClassType classType;
        private Long objectId;
    }
    @Getter
    @Setter
    public static class DeletePermisionFormAction implements Serializable {
        @NotNull
        private ClassType classType;
        private Long objectId;
    }
    public enum ClassType {
        ENTITY,
    }
}