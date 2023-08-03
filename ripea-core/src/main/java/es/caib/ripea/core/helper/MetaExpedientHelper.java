/**
 * 
 */
package es.caib.ripea.core.helper;

//import static es.caib.ripea.core.service.MetaExpedientServiceImpl.metaExpedientsAmbOrganNoSincronitzat;
import static es.caib.ripea.core.service.MetaExpedientServiceImpl.progresActualitzacio;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ActualitzacioInfo;
import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.AvisNivellEnumDto;
import es.caib.ripea.core.api.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.core.api.dto.CrearReglaResponseDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.entity.AvisEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.PermisosHelper.ListObjectIdentifiersExtractor;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.AvisRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientComentariRepository;
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
	private AvisRepository avisRepository;
    @Autowired
    private EmailHelper emailHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private DistribucioReglaHelper distribucioReglaHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private MetaExpedientComentariRepository metaExpedientComentariRepository;
	@Autowired
	private CacheHelper cacheHelper;

	public static final String PROCEDIMENT_ORGAN_NO_SYNC = "Hi ha procediments que pertanyen a òrgans no existents en l'organigrama actual";


	@Autowired
	private ExpedientRepository expedientRepository;
    
	public long obtenirProximaSequenciaExpedient(
			MetaExpedientEntity metaExpedient,
			Integer any,
			boolean incrementar) {
		
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info(
					"Obtenir proxima sequencia expedient (" +
							"metaExpedient=" + metaExpedient.getId() + " - " +metaExpedient.getCodi() + ", " +
							"any=" + any + ", " +
							"incrementar=" + incrementar + ")");
		
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
			if (cacheHelper.mostrarLogsCreacioContingut())
				logger.info("Nou sequencia creada: "+ sequencia.getAny() + ", " + sequencia.getValor() + ", "  + sequencia.getMetaExpedient().getId()+ " - " +sequencia.getMetaExpedient().getCodi());
			return sequencia.getValor();
		} else if (incrementar) {
			sequencia.incrementar();
			if (cacheHelper.mostrarLogsCreacioContingut())
				logger.info("Sequencia incrementada: " + sequencia.getAny() + ", " + sequencia.getValor() + ", " + sequencia.getMetaExpedient().getId() + " - " + sequencia.getMetaExpedient().getCodi());
			Long max = expedientRepository.findMaxSequencia(metaExpedient, any);
			long valor = sequencia.getValor();
			if (max != null && max + 1 > valor) {
				logger.error("Sequenia no correcta: valorSequenciaIncrementada=" + valor + ", maxSequenciaExp=" + max + ". Actualitzant valor de sequncia manualment...");
				sequencia.updateValor(max + 1);
			}
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
						MetaExpedientRevisioEstatEnumDto.PENDENT);
				
				emailHelper.canviEstatRevisioMetaExpedient(metaExpedientEntity, entitatId);
			}
		}
	}
	
	public void canviarRevisioADisseny(Long entitatId, Long metaExpedientId, Long organId) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, metaExpedientId, organId);

		if (metaExpedientEntity.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.DISSENY) {
			metaExpedientEntity.updateRevisioEstat(
					MetaExpedientRevisioEstatEnumDto.DISSENY);
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
	
	
	public List<MetaExpedientEntity> findProcedimentsDeOrganIDeDescendentsDeOrgan(Long organId) {

		List<OrganGestorEntity> organAmbDescendents = organGestorRepository.findOrgansAmbDescendents(Arrays.asList(organId));
		List<MetaExpedientEntity> procedimentsDeOrganIDeDescendentsDeOrgan = new ArrayList<>();
		for (OrganGestorEntity organGestorEntity : organAmbDescendents) {
			procedimentsDeOrganIDeDescendentsDeOrgan.addAll(organGestorEntity.getMetaExpedients());
		}
		return procedimentsDeOrganIDeDescendentsDeOrgan;
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
	
	
	public List<MetaExpedientEntity> getCreateWritePermesos(Long entitatId) {

		List<MetaExpedientEntity> createPermIds = findAmbPermis(
				entitatId,
				ExtendedPermission.CREATE,
				true,
				null,
				false,
				false,
				null,
				false);

		List<MetaExpedientEntity> writePermIds = findAmbPermis(
				entitatId,
				ExtendedPermission.WRITE,
				true,
				null,
				false,
				false,
				null,
				false);

		List<MetaExpedientEntity> createWritePermIds = new ArrayList<>(); 
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
				metaExpedient.getRevisioEstat());

		
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

	@Transactional
	public void actualitzarProcediments(EntitatDto entitatDto, Locale locale, ProgresActualitzacioDto progresActualitzacioDto) {
		ProgresActualitzacioDto progres = null;
		if (progresActualitzacioDto != null) {
			progres = progresActualitzacioDto;
		} else {
			progres = progresActualitzacio.get(entitatDto.getCodi());
			if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
				logger.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
				return;
			}
			// inicialitza el seguiment del prgrés d'actualització
			progres = new ProgresActualitzacioDto();
			progresActualitzacio.put(entitatDto.getCodi(), progres);
		}

		
		Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
		try {

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, false, false, false, false);
			List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
			progres.setNumOperacions(metaExpedients.size());

			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("procediment.synchronize.titol.inici")).infoText(msg("procediment.synchronize.info.inici", metaExpedients.size())).build());

			Integer organsNoSincronitzats = 0;
			Integer modificats = 0;
			Integer fallat = 0;
			for(MetaExpedientEntity metaExpedient: metaExpedients) {
				ActualitzacioInfo.ActualitzacioInfoBuilder infoBuilder = ActualitzacioInfo.builder()
						.codiSia(metaExpedient.getClassificacioSia())
						.nomAntic(metaExpedient.getNom())
						.descripcioAntiga(metaExpedient.getDescripcio())
						.comuAntic(metaExpedient.isComu());
				if (metaExpedient.getOrganGestor() != null)
					infoBuilder.organAntic(metaExpedient.getOrganGestor().getCodi());

				ProcedimentDto procedimentGga = null;
				try {
					logger.info("Procediment DB: " + metaExpedient);
					procedimentGga = pluginHelper.procedimentFindByCodiSia(
							entitat.getUnitatArrel(),
							metaExpedient.getClassificacioSia());
					infoBuilder.exist(procedimentGga != null);
					
					logger.info(" Procediment WS: " + procedimentGga);
				} catch (SistemaExternException se) {
					logger.error("Error Procediment WS id="+ metaExpedient.getId(), se);
					infoBuilder.hasError(true);
					infoBuilder.errorText(msg("procediment.synchronize.error.rolsac", se.getMessage()));
					progres.addInfo(infoBuilder.build(), true);
					fallat++;
					continue;
				}

				if (procedimentGga == null) {
					infoBuilder.hasError(true);
					infoBuilder.errorText(msg("procediment.synchronize.error.exist", metaExpedient.getClassificacioSia()));
					progres.addInfo(infoBuilder.build(), true);
					fallat++;
					continue;
				}

				ActualitzacioInfo info = infoBuilder.build();
				info.setNomNou(procedimentGga.getNom());
				info.setDescripcioNova(procedimentGga.getResum());
				info.setComuNou(procedimentGga.isComu());
				if (!procedimentGga.isComu()) {
					info.setOrganNou(procedimentGga.getUnitatOrganitzativaCodi());
				} else {
					info.setOrganNou(null);
				}
				
				if (!info.hasChange()) {
					progres.addInfo(info, true);
					continue;
				}


				String nom = procedimentGga.getNom();
				String descripcio = procedimentGga.getResum();
				OrganGestorEntity organGestor;
				boolean organNoSincronitzat = false;
				
				if (procedimentGga.isComu()) {
					organGestor = null;
				} else {
					organGestor = organGestorRepository.findByEntitatAndCodi(entitat, procedimentGga.getUnitatOrganitzativaCodi());
					if (organGestor == null) {
						organNoSincronitzat = true;
						organsNoSincronitzats++;
						organGestor = metaExpedient.getOrganGestor();
						info.setHasError(true);
						info.setErrorText(msg("procediment.synchronize.error.organ", procedimentGga.getUnitatOrganitzativaCodi()));
						fallat++;

						avisosProcedimentsOrgans.put(nom, new String[] {organGestor.getCodi() + " - " + organGestor.getNom(), procedimentGga.getUnitatOrganitzativaCodi()});
					}
				}

				metaExpedient.updateSync(nom, descripcio, organGestor, organNoSincronitzat);
				metaExpedientRepository.flush();
				progres.addInfo(info, true);
				modificats++;

			}

			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("procediment.synchronize.titol.fi")).infoText(msg("procediment.synchronize.info.fi", modificats, fallat)).build());

			progres.setProgres(100);
			if (progresActualitzacioDto == null) {
				progres.setFinished(true);
			}

//			metaExpedientsAmbOrganNoSincronitzat.put(entitat.getId(), organsNoSincronitzats);

			actualitzaAvisosSyncProcediments(avisosProcedimentsOrgans, entitatDto.getId());

		} catch (Exception e) {
			logger.error("Error al syncronitzar procediemnts", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			progresActualitzacioDto.setError(true);
			progresActualitzacioDto.setErrorMsg(sw.toString());
			progresActualitzacioDto.setProgres(100);
			progresActualitzacioDto.setFinished(true);
			throw e;
		}
	}
	


	public boolean publicarComentariPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String text, 
			String rolActual) {

		EntitatEntity entitat = null;
		if (rolActual.equals("IPA_REVISIO")) {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		} else {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, true);
		}
		
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		//truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1024);
		MetaExpedientComentariEntity comentari = MetaExpedientComentariEntity.getBuilder(
				metaExpedient, 
				text).build();
		metaExpedientComentariRepository.save(comentari);
		
		return true;
	}
	

	private void actualitzaAvisosSyncProcediments(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {
		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitatId, PROCEDIMENT_ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.delete(avisosSinc);
		}
		if (!avisosProcedimentsOrgans.isEmpty()) {
			String missatgeAvis = "";
			for(Map.Entry<String, String[]> avisProc: avisosProcedimentsOrgans.entrySet()) {
				missatgeAvis += " - Procediment '" + avisProc.getKey() + "': actualment a l'òrgan " + avisProc.getValue()[0] + ", i hauria de pertànyer a l'òrgan " + avisProc.getValue()[1] + " </br>";
			}
			missatgeAvis += "Realitzi una actualizació d'òrgans per a resoldre aquesta situació, o revisi la configuració dels procediments al repositori de procediments";
			Date ara = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ara);
			calendar.add(Calendar.YEAR, 1);
			AvisEntity avis = AvisEntity.getBuilder(
					PROCEDIMENT_ORGAN_NO_SYNC,
					missatgeAvis,
					ara,
					calendar.getTime(),
					AvisNivellEnumDto.ERROR,
					true,
					entitatId).build();
			avisRepository.save(avis);
		}
	}


	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

	private String msg(String codi) {
		return messageHelper.getMessage(codi);
	}
	private String msg(String codi, Object... params) {
		return messageHelper.getMessage(codi, params);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientHelper.class);

}
