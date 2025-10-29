package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientOrganGestorResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.AclClassResource;
import es.caib.ripea.service.intf.model.AclObjIdentityResource;
import es.caib.ripea.service.intf.model.AclSidResource;
import es.caib.ripea.service.intf.resourceservice.AclObjIdentityResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.AclResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclObjIdentityResourceServiceImpl extends BaseMutableResourceService<AclObjIdentityResource, Long, AclObjIdentityResourceEntity> implements AclObjIdentityResourceService {

    private final AclResourceHelper aclResourceHelper;
    private final MetaExpedientOrganGestorResourceRepository metaExpedientOrganGestorResourceRepository;

    @PostConstruct
    public void init() {
        register(AclObjIdentityResource.PERSPECTIVE_SID_CODE, new PermisionPerspectiveApplicator());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();

        filters.add(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty()) ? Filter.parse(currentSpringFilter) : null
        );

        Map<String, String> mapaNamedQueries = Utils.namedQueriesToMap(namedQueries);
        if (!mapaNamedQueries.isEmpty()) {
            if (mapaNamedQueries.containsKey(AclSidResource.ClassType.MET_EXP_ORG.name()) && mapaNamedQueries.get(AclSidResource.ClassType.MET_EXP_ORG.name()) != null) {
                List<Long> ids = metaExpedientOrganGestorResourceRepository
                        .findAllByMetaExpedientId(Long.valueOf(mapaNamedQueries.get(AclSidResource.ClassType.MET_EXP_ORG.name())))
                        .stream().map(BaseAuditableEntity::getId)
                        .collect(Collectors.toList());
                ids.add(Long.valueOf(0));
                Filter filtrePermisos = null;
                List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(ids);
                if (permesosClausulesIn != null) {
                    for (String aux : permesosClausulesIn) {
                        if (aux != null && !aux.isEmpty()) {
                            filtrePermisos = FilterBuilder.or(filtrePermisos, Filter.parse(AclObjIdentityResource.Fields.objectId + " IN (" + aux + ")"));
                        }
                    }
                }
                filters.add(
                        FilterBuilder.and(
                                FilterBuilder.equal(AclObjIdentityResource.Fields.classEntity + "." + AclClassResource.Fields.classname, MetaExpedientOrganGestorEntity.class.getName()),
                                FilterBuilder.isNotEmpty(AclObjIdentityResource.Fields.entries),
                                filtrePermisos
                        )
                );
            }
        }

        List<Filter> result = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    }

    private class PermisionPerspectiveApplicator implements PerspectiveApplicator<AclObjIdentityResourceEntity, AclObjIdentityResource> {
        @Override
        public void applySingle(String code, AclObjIdentityResourceEntity entity, AclObjIdentityResource resource) throws PerspectiveApplicationException {
            Map<AclSidResourceEntity, List<AclEntryResourceEntity>> entriesBySid = entity.getEntries()
                    .stream().collect(Collectors.groupingBy(AclEntryResourceEntity::getSid));

            entriesBySid.forEach((sid, entries) -> {
                AclSidResource aclSid = objectMappingHelper.newInstanceMap(sid, AclSidResource.class);
                aclSid.setOrganGestor(resource.getOrganGestor());
                aclResourceHelper.setPermisos(aclSid, entries.stream()
                        .map(AclEntryResourceEntity::getMask).collect(Collectors.toList()));
                resource.getSids().add(aclSid);
            });

            resource.getSids().sort(
                    Comparator
                            .comparing(AclSidResource::getPrincipal)
                            .thenComparing(AclSidResource::getSid)
            );
        }

        @Override
        public boolean applyMultiple(String code, List<AclObjIdentityResourceEntity> entities, List<AclObjIdentityResource> resources) throws PerspectiveApplicationException {
            List<MetaExpedientOrganGestorResourceEntity> metaExpOrgans = metaExpedientOrganGestorResourceRepository.findAllById(
                    entities.stream().map(AclObjIdentityResourceEntity::getObjectId)
                            .collect(Collectors.toList())
            );

            for (AclObjIdentityResource resource : resources) {
                metaExpOrgans.stream().filter(m-> Objects.equals(m.getId(), resource.getObjectId()))
                        .findFirst().ifPresent(m->resource.setOrganGestor(ResourceReference.toResourceReference(
                                m.getOrganGestor().getId(),
                                m.getOrganGestor().getCodiINom()
                        )));
            }

            return false;
        }
    }
}