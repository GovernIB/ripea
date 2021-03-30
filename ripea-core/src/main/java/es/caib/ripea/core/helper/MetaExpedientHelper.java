/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.PermisosHelper.ListObjectIdentifiersExtractor;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Utilitats comunes pels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MetaExpedientHelper {

	@Autowired
	private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PermisosHelper permisosHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private MetaExpedientCarpetaHelper metaExpedientCarpetaHelper;
    @Autowired
    private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized long obtenirProximaSequenciaExpedient(
			MetaExpedientEntity metaExpedient,
			Integer any,
			boolean incrementar) {
		int anyExpedient;
		if (any != null)
			anyExpedient = any.intValue();
		else
			anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		MetaExpedientSequenciaEntity sequencia = metaExpedientSequenciaRepository.findByMetaExpedientAndAny(
				metaExpedient,
				anyExpedient);
		if (sequencia == null) {
			sequencia = MetaExpedientSequenciaEntity.getBuilder(anyExpedient, metaExpedient).build();
			metaExpedientSequenciaRepository.save(sequencia);
			return sequencia.getValor();
		} else if (incrementar) {
			sequencia.incrementar();
			return sequencia.getValor();
		} else {
			return sequencia.getValor() + 1;
		}
	}

	public List<Long> findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(Long entitatId, Long organGestorId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (organGestorId == null) {
			List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitat(entitat);			
			permisosHelper.filterGrantedAnyList(
					metaExpedients,
					new ListObjectIdentifiersExtractor<MetaExpedientEntity>() {
						public List<Serializable> getObjectIdentifiers(MetaExpedientEntity metaExpedient) {
							List<Serializable> ids = new ArrayList<Serializable>();
							OrganGestorEntity organGestor = metaExpedient.getOrganGestor();
							while (organGestor != null) {
								ids.add(organGestor.getId());
								organGestor = organGestor.getPare();
							}
							return ids;
						}
					},
					OrganGestorEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					auth);
			List<Long> ids = new ArrayList<Long>();
			for (MetaExpedientEntity me : metaExpedients) {
				ids.add(me.getId());
			}
			return ids;
		} else {
			if (!permisosHelper.isGrantedAny(
							organGestorId,
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION },
							auth)) {
				return new ArrayList<Long>();
			}
			OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);			
			return metaExpedientRepository.findByOrgansGestors(organGestor.getAllChildren());
		}
	}

	public List<MetaExpedientEntity> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, 
			String filtre) {
		return findAmbOrganFiltrePermis(
				entitatId,
				organGestorId,
				ExtendedPermission.READ,
				true,
				filtre);
	}

	public List<MetaExpedientEntity> findAmbOrganFiltrePermis(
			Long entitatId,
			Long organGestorId,
			Permission permis,
			boolean nomesActius,
			String filtre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organGestorId);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
				organGestorEntity,
				!nomesActius,
				nomesActius ? true : null,
				filtre == null || "".equals(filtre.trim()),
				filtre == null ? "" : filtre);
		boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (!esAdministradorEntitat) {
			boolean organPermitted = false;
			List<OrganGestorEntity> organsPermitted = organGestorHelper.findAmbEntitatPermis(
					entitat,
					ExtendedPermission.ADMINISTRATION);
			if (organsPermitted != null && !organsPermitted.isEmpty()) {
				for (OrganGestorEntity organGestor: organsPermitted) {
					if (organGestor.getId().equals(organGestorEntity.getId())) {
						organPermitted = true;
					}
				}
			} 	
			if (!organPermitted) {
				permisosHelper.filterGrantedAll(
						metaExpedients,
						new ObjectIdentifierExtractor<MetaNodeEntity>() {
							public Long getObjectIdentifier(MetaNodeEntity metaNode) {
								return metaNode.getId();
							}
						},
						MetaNodeEntity.class,
						new Permission[] {permis},
						auth);
			}
		}
		return metaExpedients;
	}

	public List<MetaExpedientEntity> findPermesosAccioMassiva(Long entitatId, String rolActual) {
		return findAmbEntitatPermis(
				entitatId,
				ExtendedPermission.WRITE,
				true,
				null,
				"IPA_ADMIN".equals(rolActual),
				"IPA_ORGAN_ADMIN".equals(rolActual),
				null);
	}
	
	

	public List<PermisDto> permisFind(Long id) {

		List<PermisDto> permisos = new ArrayList<PermisDto>();
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(id);
		List<MetaExpedientOrganGestorEntity> metaExpedientOrgans = metaExpedientOrganGestorRepository.findByMetaExpedient(metaExpedient);
		List<Serializable> serializedIds = new ArrayList<Serializable>();
		for (MetaExpedientOrganGestorEntity metaExpedientOrgan: metaExpedientOrgans) {
			serializedIds.add(metaExpedientOrgan.getId());
		}
		Map<Serializable, List<PermisDto>> permisosOrganGestor = permisosHelper.findPermisos(serializedIds, MetaExpedientOrganGestorEntity.class);
		for (MetaExpedientOrganGestorEntity metaExpedientOrgan: metaExpedientOrgans) {
			if (permisosOrganGestor.get(metaExpedientOrgan.getId()) != null) {
				for (PermisDto permis: permisosOrganGestor.get(metaExpedientOrgan.getId())) {
					permis.setOrganGestorId(metaExpedientOrgan.getOrganGestor().getId());
					permis.setOrganGestorNom(metaExpedientOrgan.getOrganGestor().getNom());
					permisos.add(permis);
				}
			}
		}
		permisos.addAll(permisosHelper.findPermisos(id, MetaNodeEntity.class));
		return permisos;

	}
	

	public List<MetaExpedientEntity> findAmbEntitatPermis(
			Long entitatId,
			Permission permis,
			boolean nomesActius,
			String filtreNomOrCodiSia, 
			boolean isAdminEntitat,
			boolean isAdminOrgan,
			Long adminOrganOrganId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true);
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaNodeEntity.class,
				permis));
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				permis));
		organGestorHelper.afegirOrganGestorFillsIds(entitat, organIds);
		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				permis));
		organGestorHelper.afegirOrganGestorFillsIds(entitat, metaExpedientOrganIds);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndActiuAndFiltreAndPermes(
				entitat,
				!nomesActius,
				nomesActius ? nomesActius : null,
				filtreNomOrCodiSia == null || "".equals(filtreNomOrCodiSia.trim()),
				filtreNomOrCodiSia == null ? "" : filtreNomOrCodiSia,
				isAdminEntitat,
				isAdminOrgan,
				metaExpedientIds == null || metaExpedientIds.isEmpty(),
				metaExpedientIds == null || metaExpedientIds.isEmpty() ? null : metaExpedientIds,
				organIds == null || organIds.isEmpty(),
				organIds == null || organIds.isEmpty() ? null : organIds,
				metaExpedientOrganIds == null || metaExpedientOrganIds.isEmpty(),
				metaExpedientOrganIds == null || metaExpedientOrganIds.isEmpty() ? null : metaExpedientOrganIds);
		
		
/*		boolean onlyToCheckReadPermission = onlyToCheckReadPermission(permisos);

		
		if (onlyToCheckReadPermission || checkPerMassiuAdmin) {
			if (rolActual.equals("tothom")) { 
				permisosHelper.filterGrantedAll(
						metaExpedients,
						new ObjectIdentifierExtractor<MetaNodeEntity>() {
							public Long getObjectIdentifier(MetaNodeEntity metaNode) {
								return metaNode.getId();
							}
						},
						MetaNodeEntity.class,
						permisos,
						auth);
					
			} else if (rolActual.equals("IPA_ORGAN_ADMIN")) {
				permisosHelper.filterGrantedAll(
						metaExpedients,
						new ObjectIdentifierExtractor<MetaNodeEntity>() {
							public Long getObjectIdentifier(MetaNodeEntity metaNode) {
								return metaNode.getId();
							}
						},
						MetaNodeEntity.class,
						permisos,
						auth);

				List<OrganGestorEntity> organs = organGestorHelper.findOrganismesEntitatAmbPermis(entitat.getId());
				if (organs != null && !organs.isEmpty()) {
					List<MetaExpedientEntity> metaExpedientsOfOrgans = metaExpedientRepository.findByOrganGestors(
							entitat,
							organs);
					
					metaExpedients.addAll(metaExpedientsOfOrgans);
					// remove duplicates
					metaExpedients = new ArrayList<MetaExpedientEntity>(new HashSet<MetaExpedientEntity>(metaExpedients));
					
				} 
			}
			
		} else {
			permisosHelper.filterGrantedAll(
					metaExpedients,
					new ObjectIdentifierExtractor<MetaNodeEntity>() {
						public Long getObjectIdentifier(MetaNodeEntity metaNode) {
							return metaNode.getId();
						}
					},
					MetaNodeEntity.class,
					permisos,
					auth);
		}*/
		
		return metaExpedients;
	}

	public List<ArbreDto<MetaExpedientCarpetaDto>> obtenirPareArbreCarpetesPerMetaExpedient(
			MetaExpedientEntity metaExpedient,
			List<ArbreDto<MetaExpedientCarpetaDto>> carpetes) {
		//crear nova carpeta arrel
		ArbreDto<MetaExpedientCarpetaDto> carpetaPrincipal = null;
		List<MetaExpedientCarpetaDto> carpetesMetaExpedient = metaExpedientCarpetaHelper.findCarpetesMetaExpedient(metaExpedient);
		if (carpetesMetaExpedient != null && ! carpetesMetaExpedient.isEmpty()) {
			for (MetaExpedientCarpetaDto metaExpedientCarpeta: carpetesMetaExpedient) {
				if (metaExpedientCarpeta.getPare() == null) {
					carpetaPrincipal = new ArbreDto<MetaExpedientCarpetaDto>(true);
					carpetaPrincipal.setArrel(
							obtenirArbreCarpetesPerMetaExpedient(
								metaExpedientCarpeta, 
								null));
					carpetes.add(carpetaPrincipal);
				}
			}
		}
		return carpetes;
	}

	public ArbreNodeDto<MetaExpedientCarpetaDto> obtenirArbreCarpetesPerMetaExpedient(
			MetaExpedientCarpetaDto metaExpedientCarpetaDto,
			ArbreNodeDto<MetaExpedientCarpetaDto> pare) {
		ArbreNodeDto<MetaExpedientCarpetaDto> currentArbreNode =  new ArbreNodeDto<MetaExpedientCarpetaDto>(
				pare,
				metaExpedientCarpetaDto);
		// crear estructura carpetes a partir del pare actual
		for (MetaExpedientCarpetaDto fill: metaExpedientCarpetaDto.getFills()) {
			// recuperar estructura per cada fill recursivament
			currentArbreNode.addFill(
					obtenirArbreCarpetesPerMetaExpedient(
							fill,
							currentArbreNode));
		}
		return currentArbreNode;
	}

	public void crearEstructuraCarpetes(
			List<ArbreJsonDto> estructuraCarpetes,
			MetaExpedientEntity metaExpedient) {
		
		for (ArbreJsonDto carpeta: estructuraCarpetes) {
			MetaExpedientCarpetaEntity pare = null;
			crearCarpeta(
					carpeta,
					pare,
					metaExpedient);
		}
	}

	public void crearCarpeta(
			ArbreJsonDto carpeta,
			MetaExpedientCarpetaEntity pare,
			MetaExpedientEntity metaExpedient) {

		// crear carpeta actual
		Long carpetaId = null;
		try {
			carpetaId = Long.valueOf(carpeta.getId());
		} catch (NumberFormatException nfe) {}
		
		if (carpetaId != null) {
			pare = metaExpedientCarpetaHelper.actualitzarCarpeta(
					carpetaId, 
					carpeta.getText());
		} else {
			pare = metaExpedientCarpetaHelper.crearNovaCarpeta(
					carpeta.getText(),
					pare,
					metaExpedient);
		}
		
		// crear recursivament totes les carpetes
		if (!carpeta.getChildren().isEmpty()) {
			for (ArbreJsonDto subcarpeta : carpeta.getChildren()) {
				crearCarpeta(
						subcarpeta, 
						pare, 
						metaExpedient);
			}
		}
	}

	public MetaExpedientCarpetaDto deleteCarpetaMetaExpedient(Long carpetaIdJstree) {
		MetaExpedientCarpetaDto carpeta = metaExpedientCarpetaHelper.deleteCarpeta(carpetaIdJstree);
		return carpeta;
	}

	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

}
