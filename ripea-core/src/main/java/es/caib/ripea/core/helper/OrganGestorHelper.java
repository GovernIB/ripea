package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.AvisNivellEnumDto;
import es.caib.ripea.core.entity.AvisEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientOrganPareEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.AvisRepository;
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class OrganGestorHelper {

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired
	private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired
	private AvisRepository avisRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private PluginHelper pluginHelper;

	public static final String ORGAN_NO_SYNC = "Hi ha canvis pendents de sincronitzar a l'organigrama";

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

	/**
	 * Comprova si es te permís sobre un òrgan gestor, o un pare d'aquest
	 *
	 * @param organGestor
	 * @param permis
	 * @return
	 */
	public boolean isOrganGestorPermes(
			OrganGestorEntity organGestor,
			Permission permis) {

		boolean permes = false;

		boolean granted = permisosHelper.isGrantedAll(
				organGestor.getId(),
				OrganGestorEntity.class,
				new Permission[] {permis},
				SecurityContextHolder.getContext().getAuthentication());
		if (granted) {
			permes = true;
		} else {
			List<OrganGestorEntity> organGestorAmbPares = findPares(
					organGestor,
					true);
			for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
				if (permisosHelper.isGrantedAny(
						organGestorActual.getId(),
						OrganGestorEntity.class,
						new Permission[] { permis },
						SecurityContextHolder.getContext().getAuthentication())) {
					permes = true;
					break;
				}
			}
		}
		return permes;
	}

	public boolean isOrganGestorPermes(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor,
			Permission permis, 
			String rolActual) {
		
		List<OrganGestorEntity> organGestorAmbPares = findPares(
				organGestor,
				true);
		
		if (RolHelper.isAdminEntitat(rolActual)) {
			return true;
		} else if (RolHelper.isAdminOrgan(rolActual) && organGestorAmbPares.contains(organGestor)) {
			return true;
		} else {
			boolean permes = false;
			
			boolean granted = permisosHelper.isGrantedAll(
					metaExpedient.getId(),
					MetaNodeEntity.class,
					new Permission[] {permis},
					SecurityContextHolder.getContext().getAuthentication());
			if (granted) {
				permes = true;
			} else {

				for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
					if (permisosHelper.isGrantedAny(
							organGestorActual.getId(),
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
	}

	public void crearExpedientOrganPares(
			ExpedientEntity expedient,
			OrganGestorEntity organGestor) {
		List<OrganGestorEntity> organGestorAmbPares = findPares(
				organGestor,
				true);
		for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
			MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
					expedient.getMetaExpedient(),
					organGestorActual);
			if (metaExpedientOrganGestor == null) {
				logger.debug("meteaxp-organ created,  metaexp: " + expedient.getMetaExpedient().getId() + ", organ: " + organGestorActual.getId() + " " + organGestorActual.getNom());
				metaExpedientOrganGestor = metaExpedientOrganGestorRepository.save(
						MetaExpedientOrganGestorEntity.getBuilder(
								expedient.getMetaExpedient(),
								organGestorActual).build());
			}
			ExpedientOrganPareEntity expedientOrganPare = ExpedientOrganPareEntity.getBuilder(
					expedient,
					metaExpedientOrganGestor).build();
			
			logger.debug("expedient-organ-pare created, expedient: " + expedient.getId() +  ", organ: " + organGestorActual.getId() + " " + organGestorActual.getNom());
			expedientOrganPareRepository.save(expedientOrganPare);
		}
	}
	
	public void removeOldExpedientOrganPares(
			ExpedientEntity expedient,
			OrganGestorEntity organGestor) {
		
		for (ExpedientOrganPareEntity expOrgPare : expedient.getOrganGestorPares()) {
			expedientOrganPareRepository.delete(expOrgPare);
		}
		expedient.removeOrganGestorPares();

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
	
	
	public List<OrganGestorEntity> findArrelFills(
			EntitatEntity entitat,
			String filtre) {
		OrganGestorEntity organGestorEntitat = organGestorRepository.findByEntitatAndCodi(
				entitat,
				entitat.getUnitatArrel());
		List<OrganGestorEntity> organsGestors = organGestorRepository.findByEntitatAndFiltreAndPareIdIn(
				entitat,
				filtre == null,
				filtre != null ? filtre.trim() : "",
				Arrays.asList(organGestorEntitat.getId()));
//		for (OrganGestorEntity organGestorEntity : organsGestors) {
//			logger.info("organ: "+ organGestorEntity.getId() +"  "+ organGestorEntity.getCodi() + " - " + organGestorEntity.getNom());
//		}
		
		organsGestors.remove(organGestorEntitat);
		organsGestors.add(0, organGestorEntitat);
		return organsGestors;
	}
	

	public List<OrganGestorEntity> findPares(
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
	
	public List<Long> findParesIds(
			Long organGestorId,
			boolean incloureOrganGestor) {
		OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);
		List<Long> pares = new ArrayList<Long>();
		if (incloureOrganGestor) {
			pares.add(organGestor.getId());
		}
		OrganGestorEntity organGestorActual = organGestor;
		while (organGestorActual.getPare() != null) {
			organGestorActual = organGestorActual.getPare();
			pares.add(organGestorActual.getId());
		}
		return pares;
	}

	public void consultaCanvisOrganigrama(EntitatEntity entitat) {

		Date ara = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ara);
		calendar.add(Calendar.YEAR, 1);

		List<UnitatOrganitzativa> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(
				entitat.getUnitatArrel(),
				entitat.getDataActualitzacio(),
				entitat.getDataSincronitzacio());

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.delete(avisosSinc);
		}

		if (unitatsWs != null && !unitatsWs.isEmpty()) {
			AvisEntity avis = AvisEntity.getBuilder(
					ORGAN_NO_SYNC,
					"Realitzi el procés de sincronització d'òrgans gestors per a disposar dels òrgans gestors actuals.",
					ara,
					calendar.getTime(),
					AvisNivellEnumDto.ERROR,
					true,
					entitat.getId()).build();
			avisRepository.save(avis);
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

}
