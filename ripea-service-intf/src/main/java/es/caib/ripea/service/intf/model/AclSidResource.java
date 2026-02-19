package es.caib.ripea.service.intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.resourcevalidation.PermisObjectIdValid;
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
        		@ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = AclSidResource.PERSPECTIVE_PERMISION_CODE),
        		@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AclSidResource.ACTION_MODIFY_PERMISION_CODE,
                        formClass = AclSidResource.ModifyPermisionFormAction.class),
        		@ResourceArtifact(
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

    @Transient private boolean admin;// ADMINISTRATION
    @Transient private boolean adminLectura;// ADMINISTRATION_READ
    @Transient private boolean user;// READ

    @Transient private boolean read;// READ
    @Transient private boolean create;// CREATE
    @Transient private boolean write;// WRITE
    @Transient private boolean delete;// DELETE
    @Transient private boolean procedimentsComuns;// COMU
    @Transient private boolean adminComuns;// ADM_COMU
    @Transient private boolean disseny;// DISSENY
    @Transient private boolean estadistic;// STATISTICS

    @Transient private ResourceReference<OrganGestorResource, Long> organGestor;

    @JsonIgnore @Transient private List<ExtendedPermissionEnum> masks = new ArrayList<>();
    private List<ResourceReference<AclEntryResource, Long>> entries = new ArrayList<>();

    @Getter
    @Setter
    @FieldNameConstants
    @PermisObjectIdValid
    public static class ModifyPermisionFormAction extends DeletePermisionFormAction {
        @NotNull
        private PrincipalTipusEnumDto principal;
        @NotNull
        private String sid;

        @ResourceField(onChangeActive = true)
        private boolean admin;
        private boolean adminLectura;
        private boolean user;

        private boolean read;
        private boolean create;
        private boolean write;
        private boolean delete;
        private boolean procedimentsComuns;
        @ResourceField(onChangeActive = true)
        private boolean adminComuns;
        private boolean disseny;
        private boolean estadistic;
        @ResourceField(onChangeActive = true)
        @Transient private boolean all;
    }
    @Getter
    @Setter
    @FieldNameConstants
    @PermisObjectIdValid
    public static class DeletePermisionFormAction implements Serializable {
        @NotNull
        private ClassType classType;
        private Long objectId;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private Long procedimentId;
    }
    public enum ClassType {
        ENTITY,
        GRUP,
        ORGAN,
        MET_EXP_ORG,
        MET_NOD,
    }
}