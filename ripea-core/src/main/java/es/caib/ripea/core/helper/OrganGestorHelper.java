package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientOrganPareEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;

@Component
public class OrganGestorHelper {

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired
	private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired
	private PermisosHelper permisosHelper;

	public List<OrganGestorEntity> findAmbEntitatPermis(
			EntitatEntity entitat,
			Permission permis) {
		List<Serializable> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				permis);
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

	public boolean isOrganGestorPermes(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor,
			Permission permis) {
		boolean permes = false;
		
		boolean granted = permisosHelper.isGrantedAll(
				metaExpedient.getId(),
				MetaNodeEntity.class,
				new Permission[] {permis},
				SecurityContextHolder.getContext().getAuthentication());
		if (granted) {
			permes = true;
		} else {
			List<OrganGestorEntity> organGestorAmbPares = findPares(
					metaExpedient,
					organGestor,
					true);
			for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
				if (permisosHelper.isGrantedAny(
						organGestor.getId(),
						OrganGestorEntity.class,
						new Permission[] { permis },
						SecurityContextHolder.getContext().getAuthentication())) {
					permes = true;
					break;
				}
				MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
						metaExpedient,
						organGestorActual);
				if (metaExpedientOrganGestor != null && permisosHelper.isGrantedAny(
							metaExpedientOrganGestor.getId(),
							MetaExpedientOrganGestorEntity.class,
							new Permission[] { permis },
							SecurityContextHolder.getContext().getAuthentication())) {
					permes = true;
					break;
				}
			}
		}
		return permes;
	}

	public void crearExpedientOrganPares(
			ExpedientEntity expedient,
			OrganGestorEntity organGestor) {
		List<OrganGestorEntity> organGestorAmbPares = findPares(
				expedient.getMetaExpedient(),
				organGestor,
				true);
		for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
			MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
					expedient.getMetaExpedient(),
					organGestorActual);
			if (metaExpedientOrganGestor == null) {
				metaExpedientOrganGestor = metaExpedientOrganGestorRepository.save(
						MetaExpedientOrganGestorEntity.getBuilder(
								expedient.getMetaExpedient(),
								organGestorActual).build());
			}
			ExpedientOrganPareEntity ExpedientOrganPare = ExpedientOrganPareEntity.getBuilder(
					expedient,
					metaExpedientOrganGestor).build();
			expedientOrganPareRepository.save(ExpedientOrganPare);
		}
	}

	public void afegirOrganGestorFillsIds(
			EntitatEntity entitat,
			List<Long> pares) {
		if (pares != null && !pares.isEmpty()) {
			pares.addAll(organGestorRepository.findFillsIds(
					entitat,
					pares));
		}
	}

	public void afegirMetaExpedientOrganGestorFillsIds(
			EntitatEntity entitat,
			List<Long> pares) {
		pares.addAll(metaExpedientOrganGestorRepository.findFillsIds(
				entitat,
				pares));
	}

	private List<OrganGestorEntity> findPares(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor,
			boolean incloureOrganGestor) {
		List<OrganGestorEntity> pares = new ArrayList<OrganGestorEntity>();
		if (incloureOrganGestor) {
			pares.add(organGestor);
		}
		OrganGestorEntity organGestorActual = organGestor;
		while (organGestorActual.getPare() != null) {
			organGestorActual = organGestorActual.getPare();
			pares.add(organGestorActual);
		}
		return pares;
	}

}
