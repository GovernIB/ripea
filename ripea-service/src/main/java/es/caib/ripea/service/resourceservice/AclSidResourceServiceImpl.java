package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientOrganGestorResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.OrganGestorResourceRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.resourceservice.AclSidResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.AclResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclSidResourceServiceImpl extends BaseMutableResourceService<AclSidResource, Long, AclSidResourceEntity> implements AclSidResourceService {

    private final ConfigHelper configHelper;
    private final PermisosHelper permisosHelper;
    private final AclResourceHelper aclResourceHelper;
    private final EntitatResourceRepository entitatResourceRepository;
    private final OrganGestorRepository organGestorRepository;
    private final MetaExpedientOrganGestorResourceRepository metaExpedientOrganGestorResourceRepository;
    private final MetaExpedientResourceRepository metaExpedientResourceRepository;
    private final OrganGestorResourceRepository organGestorResourceRepository;
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        register(AclSidResource.PERSPECTIVE_PERMISION_CODE, new PermisionPerspectiveApplicator());
        register(AclSidResource.ACTION_MODIFY_PERMISION_CODE, new ModifyPermisionActionExecutor());
        register(AclSidResource.ACTION_DELETE_PERMISION_CODE, new DeletePermisionActionExecutor());
    }

    private Filter filterObject(String clss, String id) {
        return FilterBuilder.exists(
                FilterBuilder.and(
                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.classEntity + "." + AclClassResource.Fields.classname, clss),
                        FilterBuilder.equal(AclSidResource.Fields.entries + "." + AclEntryResource.Fields.aclObjectIdentity + "." + AclObjIdentityResource.Fields.objectId, id)
                ));
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();

        filters.add(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty()) ? Filter.parse(currentSpringFilter) : null
        );

        Map<String, String> mapaNamedQueries = Utils.namedQueriesToMap(namedQueries);
        if (!mapaNamedQueries.isEmpty()) {
            if (mapaNamedQueries.containsKey(AclSidResource.ClassType.ENTITY.name()) && mapaNamedQueries.get(AclSidResource.ClassType.ENTITY.name()) != null) {
                filters.add(filterObject(EntitatEntity.class.getName(), mapaNamedQueries.get(AclSidResource.ClassType.ENTITY.name())));
            }
            if (mapaNamedQueries.containsKey(AclSidResource.ClassType.GRUP.name()) && mapaNamedQueries.get(AclSidResource.ClassType.GRUP.name()) != null) {
                filters.add(filterObject(GrupEntity.class.getName(), mapaNamedQueries.get(AclSidResource.ClassType.GRUP.name())));
            }
            if (mapaNamedQueries.containsKey(AclSidResource.ClassType.ORGAN.name()) && mapaNamedQueries.get(AclSidResource.ClassType.ORGAN.name()) != null) {
                filters.add(filterObject(OrganGestorEntity.class.getName(), mapaNamedQueries.get(AclSidResource.ClassType.ORGAN.name())));
            }
            if (mapaNamedQueries.containsKey(AclSidResource.ClassType.MET_NOD.name()) && mapaNamedQueries.get(AclSidResource.ClassType.MET_NOD.name()) != null) {
                filters.add(filterObject(MetaNodeEntity.class.getName(), mapaNamedQueries.get(AclSidResource.ClassType.MET_NOD.name())));
            }
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

    public class PermisionPerspectiveApplicator implements PerspectiveApplicator<AclSidResourceEntity, AclSidResource> {
        @Override
        public void applySingle(String code, AclSidResourceEntity entity, AclSidResource resource) throws PerspectiveApplicationException {
            aclResourceHelper.applyPermisos(code, entity.getEntries(), resource);
        }
    }

    private class ModifyPermisionActionExecutor implements ActionExecutor<AclSidResourceEntity, AclSidResource.ModifyPermisionFormAction, Serializable> {
        @Override
        public Serializable exec(String code, AclSidResourceEntity entity, AclSidResource.ModifyPermisionFormAction params) throws ActionExecutionException {
            PermisDto permis = new PermisDto();
            permis.setPrincipalTipus(params.getPrincipal());
            permis.setPrincipalNom(params.getSid());

            switch (params.getClassType()) {
                case ENTITY:
                    permis.setAdministration(params.isAdmin());
                    permis.setAdministrationLectura(params.isAdminLectura());
                    permis.setRead(params.isUser());

                    permisosHelper.updatePermis(params.getObjectId(), EntitatEntity.class, permis);
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
                case MET_NOD:
                    permis.setAdministration(params.isAdmin());
                    permis.setRead(params.isRead());
                    permis.setCreate(params.isCreate());
                    permis.setWrite(params.isWrite());
                    permis.setDelete(params.isDelete());
                    permis.setStatistics(params.isEstadistic());
                    permisosHelper.updatePermis(params.getObjectId(), MetaNodeEntity.class, permis);
                    break;
                case MET_EXP_ORG:
                    AtomicReference<Long> id = new AtomicReference<>(params.getObjectId());
                    if (id.get() == null) {
                        Optional<MetaExpedientOrganGestorResourceEntity> metaExpOrgan = metaExpedientOrganGestorResourceRepository
                                .findByMetaExpedientIdAndOrganGestorId(params.getProcedimentId(), params.getOrganGestor().getId());

                        metaExpOrgan.ifPresentOrElse((m) -> id.set(m.getId()),
                                () -> id.set(getNewMetaExpedientOrganGestor(params.getProcedimentId(), params.getOrganGestor().getId())));
                    }

                    permis.setAdministration(params.isAdmin());
                    permis.setRead(params.isRead());
                    permis.setCreate(params.isCreate());
                    permis.setWrite(params.isWrite());
                    permis.setDelete(params.isDelete());
                    permis.setStatistics(params.isEstadistic());
                    permisosHelper.updatePermis(id.get(), MetaExpedientOrganGestorEntity.class, permis);
                    break;
            }
            return params;
        }

        private Long getNewMetaExpedientOrganGestor(Long metaExpedientId, Long organGestorId) {
            return transactionTemplate.execute(status -> {
                try {
                    MetaExpedientResourceEntity metaExpedientResourceEntity = metaExpedientResourceRepository.findById(metaExpedientId).orElse(null);
                    OrganGestorResourceEntity organGestorResourceEntity = organGestorResourceRepository.findById(organGestorId).orElse(null);
                    MetaExpedientOrganGestorResourceEntity metaExpedientOrganGestorResourceEntity = new MetaExpedientOrganGestorResourceEntity(metaExpedientResourceEntity, organGestorResourceEntity);
                    MetaExpedientOrganGestorResourceEntity savedNewMetaExpedientOrganGestorResourceEntity = metaExpedientOrganGestorResourceRepository.saveAndFlush(metaExpedientOrganGestorResourceEntity);
                    return savedNewMetaExpedientOrganGestorResourceEntity.getId();
                } catch (Exception e) {
                    throw e;
                }
            });
        }

        @Override
        public void onChange(Serializable id, AclSidResource.ModifyPermisionFormAction previous, String
                fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[]
                                     previousFieldNames, AclSidResource.ModifyPermisionFormAction target) {
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
            } else if (AclSidResource.ClassType.MET_NOD.equals(previous.getClassType())
                    || AclSidResource.ClassType.MET_EXP_ORG.equals(previous.getClassType())) {
                if (fieldName != null) {
                    switch (fieldName) {
                        case AclSidResource.ModifyPermisionFormAction.Fields.all:
                            target.setRead((Boolean) fieldValue);
                            target.setCreate((Boolean) fieldValue);
                            target.setWrite((Boolean) fieldValue);
                            target.setDelete((Boolean) fieldValue);
                            target.setEstadistic((Boolean) fieldValue);
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

            switch (params.getClassType()) {
                case ENTITY:
                    permisosHelper.updatePermis(params.getObjectId(), EntitatEntity.class, permis);
                    break;
                case GRUP:
                    permisosHelper.updatePermis(params.getObjectId(), GrupEntity.class, permis);
                    break;
                case ORGAN:
                    permisosHelper.updatePermis(params.getObjectId(), OrganGestorEntity.class, permis);
                    break;
                case MET_NOD:
                    permisosHelper.updatePermis(params.getObjectId(), MetaNodeEntity.class, permis);
                    break;
                case MET_EXP_ORG:
                    Optional<MetaExpedientOrganGestorResourceEntity> metaExpOrgan = metaExpedientOrganGestorResourceRepository
                            .findByMetaExpedientIdAndOrganGestorId(params.getProcedimentId(), params.getOrganGestor().getId());
                    metaExpOrgan.ifPresent(m -> permisosHelper.updatePermis(m.getId(), MetaExpedientOrganGestorEntity.class, permis) );
                    break;
            }
            return objectMappingHelper.newInstanceMap(entity, AclSidResource.class);
        }

        @Override
        public void onChange(Serializable id, AclSidResource.DeletePermisionFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AclSidResource.DeletePermisionFormAction target) {
        }
    }
}