package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo.PermisosEntitat;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.UsuariResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Map;
import java.util.Optional;

/**
 * Implementació del servei de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity> implements UsuariResourceService {

    private final UsuariResourceHelper usuariResourceHelper;

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	Filter filtreBase = (Utils.hasValue(currentSpringFilter))?Filter.parse(currentSpringFilter):null;
//    	Filter filtreNif = FilterBuilder.isNotNull(UsuariResource.Fields.nif);
    	Filter filtreNom1 = FilterBuilder.not(FilterBuilder.like(UsuariResource.Fields.codi, "%SYSTEM%"));
    	Filter filtreNom2 = FilterBuilder.not(FilterBuilder.like(UsuariResource.Fields.codi, "$%"));
    	Filter filtreResultat = FilterBuilder.and(filtreBase, filtreNom1, filtreNom2);
    	return filtreResultat.generate();
    }
    
    @Transactional(readOnly = true)
    @Override
    public UserPermissionInfo getCurrentUserPermissionInfo() {

        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuariResourceEntity usuari = getEntity(usuariCodi, null);
        if (usuari == null) {
            throw new ResourceNotFoundException(UsuariResource.class, usuariCodi);
        }

        String usuariNom = usuari.getNom();
        boolean superusuari = RolHelper.doesCurrentUserHasRol(BaseConfig.ROLE_SUPER);
        Map<Long, PermisosEntitat> permisosEntitat = usuariResourceHelper.getPermisosEntitat(usuariCodi);

        return UserPermissionInfo.builder()
                .codi(usuariCodi)
                .nom(usuariNom)
                .conf(objectMappingHelper.newInstanceMap(usuari, UsuariResource.class))
                .superusuari(superusuari)
                .permisosEntitat(permisosEntitat)
                .build();
    }

    @Override
    protected UsuariResourceEntity getEntity(String id, String[] perspectives) throws ResourceNotFoundException {
        Optional<UsuariResourceEntity> result;
        Specification<UsuariResourceEntity> pkSpec = hasCodi(id);
        String additionalSpringFilter = additionalSpringFilter(null, null);
        if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
            result = entityRepository.findOne(pkSpec.and(getSpringFilterSpecification(additionalSpringFilter)));
        } else {
            result = entityRepository.findOne(pkSpec);
        }
        if (result.isPresent()) {
            return result.get();
        } else {
            String idToString = id != null ? id.toString() : "<null>";
            String idMessage = idToString;
            if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
                idMessage = "{id=" + idToString + ", springFilter=" + additionalSpringFilter + "}";
            }
            throw new ResourceNotFoundException(UsuariResource.class, idMessage);
        }
    }

    public static Specification<UsuariResourceEntity> hasCodi(String id) {
        return (Root<UsuariResourceEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.equal(root.get("codi"), id);
        };
    }

}
