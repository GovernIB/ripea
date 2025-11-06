package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.permission.ExtendedPermission;

/**
 * Helper creat per no tenir dependencies cicliques entre helpers autowired a OrganGestorHelper
 */
@Component
public class OrganismeHelper {

	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private CacheHelper cacheHelper;
	
	public List<OrganismeDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			EntitatEntity entitat,
			Long metaExpedientId,
			Permission permis,
			String filtre,
			Long expedientId,
			String rolActual,
			Long organActualId) {

		List<OrganismeDto> organsGestors = null;
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findById(metaExpedientId).orElse(null);
		
		if (RolHelper.isAdminEntitat(rolActual)) {
			organsGestors = organGestorHelper.findArrelFills(entitat.getCodi(), filtre);
		} else if (RolHelper.isAdminOrgan(rolActual)) {
			organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), organActualId, filtre);
		} else {

			if (metaExpedient.getOrganGestor() != null) {
				// S'han de retornar els fills de l'òrgan gestor del metaExpedient si l'usuari actual
				// te permisos per l'òrgan gestor.
				organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), metaExpedient.getOrganGestor().getId(), filtre);
			} else {

				Set<String> organCodis = new HashSet<>();
				// Cercam las parelles metaExpedient-organ amb permisos assignats
				List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors = metaExpedientOrganGestorRepository.findByMetaExpedient(metaExpedient);
				if (metaExpedientOrgansGestors != null && !metaExpedientOrgansGestors.isEmpty()) {
					permisosHelper.filterGrantedAll(
							metaExpedientOrgansGestors,
							MetaExpedientOrganGestorEntity.class,
							new Permission[]{permis});
					if (!metaExpedientOrgansGestors.isEmpty()) {
						organCodis.addAll(metaExpedientOrganGestorRepository.findOrganGestorCodisByMetaExpedientOrganGestors(metaExpedientOrgansGestors));
					}
				}
				// Cercam els òrgans amb permisos per procediments comuns
				if (metaExpedient.getOrganGestor() == null) {
					List<Long> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(OrganGestorEntity.class, ExtendedPermission.COMU, permis);
					if (organProcedimentsComunsIds != null && !organProcedimentsComunsIds.isEmpty()) {
						organCodis.addAll(organGestorRepository.findCodisByIdList(entitat.getId(), organProcedimentsComunsIds));
					}
				}
				organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), new ArrayList<>(organCodis), filtre);

				// Si l'usuari actual te permis direct al metaExpedient, automaticament te permis per tots unitats fills del entitat
				if (organsGestors == null || organsGestors.isEmpty()) {
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					boolean metaNodeHasPermis = permisosHelper.isGrantedAll(
							metaExpedientId,
							MetaNodeEntity.class,
							new Permission[]{permis},
							auth);
					if (metaNodeHasPermis) {
						organsGestors = organGestorHelper.findArrelFills(entitat.getCodi(), filtre);
					}
				}
			}

			// if we modify expedient we have to ensure that we can still see its organ in dropdown even if permissions were removed
			if (expedientId != null) {
				ExpedientEntity expedientEntity = entityComprovarHelper.comprovarExpedient(
						expedientId,
						false,
						false,
						false,
						false,
						false,
						null);

				OrganGestorEntity organGestorEntity = expedientEntity.getOrganGestor();

				if (organsGestors == null) {
					organsGestors = new ArrayList<>();
				}
				boolean alreadyInTheList = false;
				for (OrganismeDto organGestor : organsGestors) {
					if (organGestor.getId().equals(organGestorEntity.getId())) {
						alreadyInTheList = true;
					}
				}
				if (!alreadyInTheList) {
					organsGestors.add(0, cacheHelper.findOrganigramaByEntitat(entitat.getCodi()).get(organGestorEntity.getCodi()));
				}
			}

		}
		return organsGestors;
	}
}
