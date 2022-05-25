/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.core.api.dto.CrearReglaResponseDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.PermisosHelper.ListObjectIdentifiersExtractor;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientSequenciaRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
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
	private MetaNodeRepository metaNodeRepository;
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
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private MetaExpedientTascaRepository metaExpedientTascaRepository;
    @Autowired
    private EmailHelper emailHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private DistribucioReglaHelper distribucioReglaHelper;
    
	public long obtenirProximaSequenciaExpedient(
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

	public List<Long> findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(Long entitatId, Long organGestorId, boolean hasPermisAdmComu) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
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
			List<Long> procedimentIds = new ArrayList<>();
			if (hasPermisAdmComu) {
				procedimentIds = metaExpedientRepository.findProcedimentsComunsActiveIds(entitat);
			}
			if (!permisosHelper.isGrantedAny(
							organGestorId,
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION },
							auth)) {
				return procedimentIds;
			}
			OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);
			List<Long> procedimentsByOrganGestor = metaExpedientRepository.findByOrgansGestors(organGestor.getAllChildren());
			if (procedimentsByOrganGestor != null)
				procedimentIds.addAll(procedimentsByOrganGestor);
			return procedimentIds;
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
		return findAmbPermis(
				entitatId,
				ExtendedPermission.WRITE,
				true,
				null,
				"IPA_ADMIN".equals(rolActual),
				"IPA_ORGAN_ADMIN".equals(rolActual),
				null, 
				false);
	}
	
	

	public List<PermisDto> permisFind(Long id) {

		MetaExpedientEntity metaExpedient = null;
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		MetaNodeEntity metaNode = metaNodeRepository.getOne(id);
		if (metaNode instanceof MetaExpedientEntity) {
			metaExpedient = (MetaExpedientEntity) metaNode;
		} else if (metaNode instanceof MetaDocumentEntity){
			metaExpedient = ((MetaDocumentEntity) metaNode).getMetaExpedient();
		}
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
					permis.setOrganGestorCodi(metaExpedientOrgan.getOrganGestor().getCodi());
					permisos.add(permis);
				}
			}
		}
		permisos.addAll(permisosHelper.findPermisos(id, MetaNodeEntity.class));
		return permisos;

	}
	

	public List<MetaExpedientEntity> findAmbPermis(
			Long entitatId,
			Permission permis,
			boolean nomesActius,
			String filtreNomOrCodiSia, 
			boolean isAdminEntitat,
			boolean isAdminOrgan,
			Long organId, 
			boolean comu) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaNodeEntity.class,
				permis));
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				isAdminOrgan ? ExtendedPermission.ADMINISTRATION : permis));
		organGestorHelper.afegirOrganGestorFillsIds(entitat, organIds);
		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				permis));
		organGestorHelper.afegirOrganGestorFillsIds(entitat, metaExpedientOrganIds);
		
		// Cercam els òrgans amb permisos per procediemnts comuns
		List<Serializable> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(
				OrganGestorEntity.class,
				ExtendedPermission.COMU,
				permis);
		boolean accessAllComu = false;
		if (organProcedimentsComunsIds != null && !organProcedimentsComunsIds.isEmpty()) {
			accessAllComu = true;
		}

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
				metaExpedientOrganIds == null || metaExpedientOrganIds.isEmpty() ? null : metaExpedientOrganIds, 
				isRevisioActiva(),
				comu && organId != null,
				organId != null ? organGestorRepository.findOne(organId) : null,
				accessAllComu);
		
		
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
	
	

	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException {
		logger.debug(
				"Creant una nova tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "metaExpedientTasca=" + metaExpedientTasca + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		Long idEstatCrear = metaExpedientTasca.getEstatIdCrearTasca();
		ExpedientEstatEntity estatCrearTasca = idEstatCrear != null ? expedientEstatRepository.findOne(idEstatCrear) : null;
		Long idEstatFinalitzar = metaExpedientTasca.getEstatIdFinalitzarTasca();
		ExpedientEstatEntity estatFinalitzarTasca = idEstatFinalitzar != null ? expedientEstatRepository.findOne(
				idEstatFinalitzar) : null;
		MetaExpedientTascaEntity entity = MetaExpedientTascaEntity.getBuilder(
				metaExpedientTasca.getCodi(),
				metaExpedientTasca.getNom(),
				metaExpedientTasca.getDescripcio(),
				metaExpedientTasca.getResponsable(),
				metaExpedient,
				metaExpedientTasca.getDataLimit(),
				estatCrearTasca,
				estatFinalitzarTasca).build();
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(metaExpedientTascaRepository.save(entity), MetaExpedientTascaDto.class);
	}
	

	public void canviarRevisioAPendentEnviarEmail(Long entitatId, Long metaExpedientId, Long organId) {

		boolean revisioActiva = configHelper.getAsBoolean("es.caib.ripea.metaexpedients.revisio.activa");
		
		if (revisioActiva) {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
			MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, metaExpedientId, organId);

			if (metaExpedientEntity.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.PENDENT) {
				metaExpedientEntity.updateRevisioEstat(
						MetaExpedientRevisioEstatEnumDto.PENDENT,
						null);
				
				emailHelper.canviEstatRevisioMetaExpedient(metaExpedientEntity, entitatId);
			}
		}
	}
	
	public void canviarRevisioADisseny(Long entitatId, Long metaExpedientId, Long organId) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, metaExpedientId, organId);

		if (metaExpedientEntity.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.DISSENY) {
			metaExpedientEntity.updateRevisioEstat(
					MetaExpedientRevisioEstatEnumDto.DISSENY,
					null);
			// No s'envia email mentre el meta-expedient està en DISSENY;
			// s'enviarà quan el IPA_ORGAN_ADMIN canviï el seu estat a PENDENT
		}
	}
	
	public boolean isRevisioActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.metaexpedients.revisio.activa");
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
	
	
	
	public List<Long> getIdsCreateWritePermesos(Long entitatId) {
		
		List<Long> createPermIds = getIds(
				findAmbPermis(
					entitatId,
					ExtendedPermission.CREATE,
					true,
					null,
					false,
					false,
					null, 
					false));

		List<Long> writePermIds = getIds(
				findAmbPermis(
					entitatId,
					ExtendedPermission.WRITE,
					true,
					null,
					false,
					false,
					null, 
					false));

		List<Long> createWritePermIds = new ArrayList<>(); 
		createWritePermIds.addAll(createPermIds);
		createWritePermIds.addAll(writePermIds);
		createWritePermIds = new ArrayList<>(new HashSet<>(createWritePermIds));
		if (createWritePermIds.isEmpty()) {
			createWritePermIds = null;
		}
		
		return createWritePermIds;
		
	}
	
	public List<Long> getIds(List<MetaExpedientEntity> entities) {
		List<Long> ids = new ArrayList<>();
		for (MetaExpedientEntity entity : entities) {
			ids.add(entity.getId());
		}
		return ids;
	}
	
	

	public MetaExpedientDto canviarEstatRevisioASellecionat(Long entitatId, MetaExpedientDto metaExpedient) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedient.getId());

		MetaExpedientRevisioEstatEnumDto estatAnterior = metaExpedientEntity.getRevisioEstat();
		
		metaExpedientEntity.updateRevisioEstat(
				metaExpedient.getRevisioEstat(),
				metaExpedient.getRevisioComentari());

		
		if (estatAnterior == MetaExpedientRevisioEstatEnumDto.PENDENT && metaExpedient.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.PENDENT 
				&& metaExpedient.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.DISSENY) {

			emailHelper.canviEstatRevisioMetaExpedient(metaExpedientEntity, entitatId);

		}
		
		if (estatAnterior == MetaExpedientRevisioEstatEnumDto.PENDENT && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.DISSENY) {

			emailHelper.canviEstatRevisioMetaExpedientEnviarAAdminOrganCreador(metaExpedientEntity, entitatId);

		}

		return conversioTipusHelper.convertir(metaExpedientEntity, MetaExpedientDto.class);
	}
	
	
	
	public CrearReglaResponseDto crearReglaDistribucio(Long metaExpedientId) {
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		metaExpedient.updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PENDENT);

		try {

			CrearReglaResponseDto rearReglaResponseDto = distribucioReglaHelper.crearRegla(
					metaExpedient.getEntitat().getUnitatArrel(),
					metaExpedient.getClassificacioSia());

			if (rearReglaResponseDto.getStatus() == StatusEnumDto.OK) {
				metaExpedient.updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PROCESSAT);
			} else {
				metaExpedient.updateCrearReglaDistribucioError(StringUtils.abbreviate(rearReglaResponseDto.getMsg(), 1024));
			}

			return rearReglaResponseDto;

		} catch (Exception e) {
			logger.error("Error al crear regla en distribucio ", e);
			metaExpedient.updateCrearReglaDistribucioError(StringUtils.abbreviate(e.getMessage() + ": " + ExceptionUtils.getStackTrace(e), 1024));

			return new CrearReglaResponseDto(StatusEnumDto.ERROR,
					ExceptionHelper.getRootCauseOrItself(e).getMessage());
		}
	}

	

		

	
	

	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientHelper.class);

}
