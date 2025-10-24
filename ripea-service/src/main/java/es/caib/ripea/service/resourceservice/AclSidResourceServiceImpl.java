package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.entity.AclSidEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.persistence.repository.AclSidRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.resourceservice.AclSidResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclSidResourceServiceImpl extends BaseMutableResourceService<AclSidResource, Long, AclSidResourceEntity> implements AclSidResourceService {

    private final ConfigHelper configHelper;
    private final PermisosHelper permisosHelper;
    private final EntitatResourceRepository entitatResourceRepository;
    private final OrganGestorRepository organGestorRepository;

    @PostConstruct
    public void init() {
        register(AclSidResource.PERSPECTIVE_PERMISION_CODE, new PermisionPerspectiveApplicator());
        register(AclSidResource.ACTION_MODIFY_PERMISION_CODE, new ModifyPermisionActionExecutor());
        register(AclSidResource.ACTION_DELETE_PERMISION_CODE, new DeletePermisionActionExecutor());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        boolean undo = true;
        List<Filter> filters = new ArrayList<>();

        filters.add(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty()) ? Filter.parse(currentSpringFilter) : null
        );

        Map<String, String> mapaNamedQueries = Utils.namedQueriesToMap(namedQueries);
        if (!mapaNamedQueries.isEmpty()) {
            if (mapaNamedQueries.containsKey("ENTITY")) {
                undo = false;
                String entitatActualCodi = configHelper.getEntitatActualCodi();
                EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
                filters.add(
                        FilterBuilder.exists(
                                FilterBuilder.and(
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.classEntity + "." + AclClassResource.Fields.classname, EntitatEntity.class.getName()),
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.objectId, entitat.getId())
                                ))
                );
            }
            if (mapaNamedQueries.containsKey("GRUP") && mapaNamedQueries.get("GRUP") != null) {
                undo = false;
                filters.add(
                        FilterBuilder.exists(
                                FilterBuilder.and(
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.classEntity + "." + AclClassResource.Fields.classname, GrupEntity.class.getName()),
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.objectId, mapaNamedQueries.get("GRUP"))
                                ))
                );
            }
            if (mapaNamedQueries.containsKey("ORGAN") && mapaNamedQueries.get("ORGAN") != null) {
                undo = false;
                filters.add(
                        FilterBuilder.exists(
                                FilterBuilder.and(
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.classEntity + "." + AclClassResource.Fields.classname, OrganGestorEntity.class.getName()),
                                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.objectId, mapaNamedQueries.get("ORGAN"))
                                ))
                );
            }
        }

        if (undo) {
//            filters.add(
//                    FilterBuilder.equal("id", 0)
//            );
        }
        List<Filter> result = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    }

    @Override
    protected PerspectiveApplicator<AclSidResourceEntity, AclSidResource> getPerspectiveApplicator(String code) {
        return super.getPerspectiveApplicator(code.split("#")[0]);
    }

    public List<AclEntryResourceEntity> filterEntries(List<AclEntryResourceEntity> entities, String classname, Long objectId) {
        return entities.stream()
                .filter(entry ->
                        entry.getAclObjectIdentity().getClassEntity().getClassname().equals(classname)
                                && entry.getAclObjectIdentity().getObjectId().equals(objectId)
                )
                .collect(Collectors.toList());
    }

    private class PermisionPerspectiveApplicator implements PerspectiveApplicator<AclSidResourceEntity, AclSidResource> {
        @Override
        public void applySingle(String code, AclSidResourceEntity entity, AclSidResource resource) throws PerspectiveApplicationException {
            if (code.contains("ENTITY")) {
                String entitatActualCodi = configHelper.getEntitatActualCodi();
                EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
                resource.setMasks(filterEntries(entity.getEntries(), EntitatEntity.class.getName(), entitat.getId()).stream()
                        .map(AclEntryResourceEntity::getMask)
                        .collect(Collectors.toList()));
            }
            if (code.contains("GRUP") && code.split("#").length == 3) {
                resource.setMasks(filterEntries(entity.getEntries(), GrupEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                        .map(AclEntryResourceEntity::getMask)
                        .collect(Collectors.toList()));
            }
            if (code.contains("ORGAN") && code.split("#").length == 3) {
                resource.setMasks(filterEntries(entity.getEntries(), OrganGestorEntity.class.getName(), Long.valueOf(code.split("#")[2])).stream()
                        .map(AclEntryResourceEntity::getMask)
                        .collect(Collectors.toList()));
            }
            resource.setAdmin(resource.getMasks().contains(ExtendedPermissionEnum.ADMINISTRATION));
            resource.setAdminLectura(resource.getMasks().contains(ExtendedPermissionEnum.ADMINISTRATION_READ));
            resource.setUser(resource.getMasks().contains(ExtendedPermissionEnum.READ));
            resource.setRead(resource.getMasks().contains(ExtendedPermissionEnum.READ));
            resource.setCreate(resource.getMasks().contains(ExtendedPermissionEnum.CREATE));
            resource.setWrite(resource.getMasks().contains(ExtendedPermissionEnum.WRITE));
            resource.setDelete(resource.getMasks().contains(ExtendedPermissionEnum.DELETE));
            resource.setProcedimentsComuns(resource.getMasks().contains(ExtendedPermissionEnum.COMU));
            resource.setAdminComuns(resource.getMasks().contains(ExtendedPermissionEnum.ADM_COMU));
            resource.setDisseny(resource.getMasks().contains(ExtendedPermissionEnum.DISSENY));
        }
    }

    private class ModifyPermisionActionExecutor implements ActionExecutor<AclSidResourceEntity, AclSidResource.ModifyPermisionFormAction, Serializable> {
        @Override
        public Serializable exec(String code, AclSidResourceEntity entity, AclSidResource.ModifyPermisionFormAction params) throws ActionExecutionException {
            PermisDto permis = new PermisDto();
            permis.setPrincipalTipus(params.getPrincipal());
            permis.setPrincipalNom(params.getSid());

            switch (params.getClassType()){
                case ENTITY:
                    permis.setAdministration(params.isAdmin());
                    permis.setAdministrationLectura(params.isAdminLectura());
                    permis.setRead(params.isUser());

                    String entitatActualCodi = configHelper.getEntitatActualCodi();
                    EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
                    permisosHelper.updatePermis(entitat.getId(), EntitatEntity.class, permis);
                    break;
                case GRUP:
                    permis.setRead(true);
                    permisosHelper.updatePermis(params.getObjectId(), GrupEntity.class, permis);
                    break;
                case ORGAN:
                    permis.setAdministration(params.isAdmin());
                    permis.setRead(params.isRead());
                    permis.setCreate(params.isCreate());
                    permis.setWrite(params.isWrite());
                    permis.setDelete(params.isDelete());
                    permis.setProcedimentsComuns(params.isProcedimentsComuns());
                    permis.setAdministrationComuns(params.isAdminComuns());
                    permis.setDisseny(params.isDisseny());
                    permisosHelper.updatePermis(params.getObjectId(), OrganGestorEntity.class, permis);
                    break;
            }
            return params;
        }

        @Override
        public void onChange(Serializable id, AclSidResource.ModifyPermisionFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AclSidResource.ModifyPermisionFormAction target) {
            if (AclSidResource.ClassType.ORGAN.equals(previous.getClassType())) {
                if (fieldName == null) {
                    if (previous.getOrganGestor() == null) {
                        organGestorRepository.findById(previous.getObjectId())
                                .ifPresent((organ) -> {
                                    target.setOrganGestor(ResourceReference.toResourceReference(
                                            organ.getId(),
                                            organ.getCodiINom()
                                    ));
                                });
                    }
                } else {
                    switch (fieldName) {
                        case AclSidResource.ModifyPermisionFormAction.Fields.adminComuns:
                            if ((Boolean) fieldValue)
                                target.setAdmin(true);
                            break;
                        case AclSidResource.ModifyPermisionFormAction.Fields.admin:
                            if ((Boolean) fieldValue)
                                target.setAll(true);
                            break;
                        case AclSidResource.ModifyPermisionFormAction.Fields.all:
                            target.setRead((Boolean) fieldValue);
                            target.setCreate((Boolean) fieldValue);
                            target.setWrite((Boolean) fieldValue);
                            target.setDelete((Boolean) fieldValue);
                            break;
                    }
                }
            }
        }
    }

    private class DeletePermisionActionExecutor implements ActionExecutor<AclSidResourceEntity, AclSidResource.DeletePermisionFormAction, Serializable> {
        @Override
        public Serializable exec(String code, AclSidResourceEntity entity, AclSidResource.DeletePermisionFormAction params) throws ActionExecutionException {
            PermisDto permis = new PermisDto();
            permis.setPrincipalTipus(entity.getPrincipal());
            permis.setPrincipalNom(entity.getSid());

            switch (params.getClassType()){
                case ENTITY:
                    String entitatActualCodi = configHelper.getEntitatActualCodi();
                    EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
                    permisosHelper.updatePermis(entitat.getId(), EntitatEntity.class, permis);
                    break;
                case GRUP:
                    permisosHelper.updatePermis(params.getObjectId(), GrupEntity.class, permis);
                    break;
                case ORGAN:
                    permisosHelper.updatePermis(params.getObjectId(), OrganGestorEntity.class, permis);
                    break;
            }
            return objectMappingHelper.newInstanceMap(entity, AclSidResource.class);
        }

        @Override
        public void onChange(Serializable id, AclSidResource.DeletePermisionFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AclSidResource.DeletePermisionFormAction target) {
        }
    }
}