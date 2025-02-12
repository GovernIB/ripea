/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.service.OrganGestorService;
import es.caib.ripea.service.permission.ExtendedPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utilitat per omplir els permisos de les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosEntitatHelper {

	@Resource
	private PermisosHelper permisosHelper;
	@Autowired
	private OrganGestorService organGestorService;

	
	public void omplirPermisosPerEntitats(List<EntitatDto> entitats, boolean ambLlistaPermisos) {
		// Filtra les entitats per saber els permisos per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<EntitatDto> oie = new ObjectIdentifierExtractor<EntitatDto>() {

			public Long getObjectIdentifier(EntitatDto entitat) {
				return entitat.getId();
			}

		};
		List<EntitatDto> entitatsRead = new ArrayList<EntitatDto>();
		entitatsRead.addAll(entitats);
		permisosHelper.filterGrantedAll(
				entitatsRead,
				oie,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.READ },
				auth);
		List<EntitatDto> entitatsAdministracio = new ArrayList<EntitatDto>();
		entitatsAdministracio.addAll(entitats);
		permisosHelper.filterGrantedAll(
				entitatsAdministracio,
				oie,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);

		for (EntitatDto entitat : entitats) {
			entitat.setUsuariActualRead(entitatsRead.contains(entitat));
			entitat.setUsuariActualAdministration(entitatsAdministracio.contains(entitat));		
			entitat.setOrgansGestors(organGestorService.findOrganismesEntitatAmbPermis(entitat.getId()));
		}
		// Obté els permisos per a totes les entitats només amb una consulta
		if (ambLlistaPermisos) {
			List<Serializable> ids = new ArrayList<Serializable>();
			for (EntitatDto entitat: entitats) {
				ids.add(entitat.getId());
			}
			Map<Serializable, List<PermisDto>> permisos = permisosHelper.findPermisos(ids, EntitatEntity.class);
			for (EntitatDto entitat : entitats)
				entitat.setPermisos(permisos.get(entitat.getId()));
		}
	}

	public void omplirPermisosPerEntitat(EntitatDto entitat) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		entitat.setUsuariActualRead(
				permisosHelper.isGrantedAll(
						entitat.getId(),
						EntitatEntity.class,
						new Permission[] { ExtendedPermission.READ },
						auth));
		entitat.setUsuariActualAdministration(
				permisosHelper.isGrantedAll(
						entitat.getId(),
						EntitatEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION },
						auth));
		entitat.setOrgansGestors(organGestorService.findOrganismesEntitatAmbPermis(entitat.getId()));
	}
}
