package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.entity.resourceentity.AclEntryResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.AclSidResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.model.AclSidResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AclResourceHelper {

    private final ConfigHelper configHelper;
    private final EntitatResourceRepository entitatResourceRepository;

    public List<AclEntryResourceEntity> filterEntries(List<AclEntryResourceEntity> entities, String classname, Long objectId) {
        return entities.stream()
                .filter(entry ->
                        entry.getAclObjectIdentity().getClassEntity().getClassname().equals(classname)
                                && entry.getAclObjectIdentity().getObjectId().equals(objectId)
                )
                .collect(Collectors.toList());
    }

    public void applyPermisos(String code, List<AclEntryResourceEntity> entities, AclSidResource resource) throws PerspectiveApplicationException {
        if (code.contains(AclSidResource.ClassType.ENTITY.name()) && code.split("#").length == 3) {
            resource.setMasks(filterEntries(entities, EntitatEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                    .map(AclEntryResourceEntity::getMask)
                    .collect(Collectors.toList()));
        }
        if (code.contains(AclSidResource.ClassType.GRUP.name()) && code.split("#").length == 3) {
            resource.setMasks(filterEntries(entities, GrupEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                    .map(AclEntryResourceEntity::getMask)
                    .collect(Collectors.toList()));
        }
        if (code.contains(AclSidResource.ClassType.ORGAN.name()) && code.split("#").length == 3) {
            resource.setMasks(filterEntries(entities, OrganGestorEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                    .map(AclEntryResourceEntity::getMask)
                    .collect(Collectors.toList()));
        }
        if (code.contains(AclSidResource.ClassType.MET_NOD.name()) && code.split("#").length == 3) {
            resource.setMasks(filterEntries(entities, MetaNodeEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                    .map(AclEntryResourceEntity::getMask)
                    .collect(Collectors.toList()));
        }
        setPermisos(resource, resource.getMasks());
    }

    public void setPermisos(AclSidResource resource, List<ExtendedPermissionEnum> masks){
        resource.setAdmin(masks.contains(ExtendedPermissionEnum.ADMINISTRATION));
        resource.setAdminLectura(masks.contains(ExtendedPermissionEnum.ADMINISTRATION_READ));
        resource.setUser(masks.contains(ExtendedPermissionEnum.READ));
        resource.setRead(masks.contains(ExtendedPermissionEnum.READ));
        resource.setCreate(masks.contains(ExtendedPermissionEnum.CREATE));
        resource.setWrite(masks.contains(ExtendedPermissionEnum.WRITE));
        resource.setDelete(masks.contains(ExtendedPermissionEnum.DELETE));
        resource.setProcedimentsComuns(masks.contains(ExtendedPermissionEnum.COMU));
        resource.setAdminComuns(masks.contains(ExtendedPermissionEnum.ADM_COMU));
        resource.setDisseny(masks.contains(ExtendedPermissionEnum.DISSENY));
        resource.setEstadistic(masks.contains(ExtendedPermissionEnum.STATISTICS));
    }

}
