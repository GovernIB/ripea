package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;

@Component
public class OrganGestorHelper {

    @Autowired
    private EntityComprovarHelper entityComprovarHelper;

    @Resource
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PermisosHelper permisosHelper;

    public List<OrganGestorEntity> findOrganismesEntitatAmbPermis(Long entitatId) {
    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
		List<Serializable> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				ExtendedPermission.ADMINISTRATION);
		if (objectsIds == null || objectsIds.isEmpty()) {
			return new ArrayList<OrganGestorEntity>();
		} else {
			List<Long> objectsIdsTypeLong = new ArrayList<Long>();
			for (Serializable oid: objectsIds) {
				objectsIdsTypeLong.add((Long)oid);
			}
			return organGestorRepository.findByEntitatAndIds(entitat, objectsIdsTypeLong);
		}
    }

}
