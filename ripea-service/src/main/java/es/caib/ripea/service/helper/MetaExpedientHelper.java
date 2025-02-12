/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.helper.PermisosHelper.ListObjectIdentifiersExtractor;
import es.caib.ripea.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import org.apache.commons.collections4.ListUtils;
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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

import static es.caib.ripea.service.service.MetaExpedientServiceImpl.progresActualitzacio;

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
	@Autowired
	private MetaNodeHelper metaNodeHelper;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;

	public static final String PROCEDIMENT_ORGAN_NO_SYNC = "Hi ha procediments que pertanyen a òrgans no existents en l'organigrama actual";


	@Autowired
	private ExpedientRepository expedientRepository;
    @Autowired
    private OrganGestorCacheHelper organGestorCacheHelper;

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
			List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
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
							new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.DISSENY },
							auth)) {
				return procedimentIds;
			}
			OrganGestorEntity organGestor = organGestorRepository.getOne(organGestorId);
			
			List<OrganGestorEntity> all = organGestor.getAllChildren();
			// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
			List<List<OrganGestorEntity>> sublists = ListUtils.partition(all, 1000);
			List<Long> procedimentsByOrganGestor = new ArrayList<>();
			for (List<OrganGestorEntity> sublist : sublists) {
				procedimentsByOrganGestor.addAll(metaExpedientRepository.findByOrgansGestors(sublist));
			}

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
		OrganGestorEntity organGestorEntity = organGestorRepository.getOne(organGestorId);
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

		long t0 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis start ( entitatId=" + entitatId + 
					", nomesActius=" + nomesActius + ", filtreNomOrCodiSia=" + filtreNomOrCodiSia + ", isAdminEntitat=" + isAdminEntitat + 
					", isAdminOrgan=" + isAdminOrgan + ", organId=" + organId + ", comu=" + comu +") ");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		
		long t1 = System.currentTimeMillis();
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIds = toListLong(permisosHelper.getObjectsIdsWithPermission(MetaNodeEntity.class, permis));
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis metaExpedientIds (" + (Utils.isNotEmpty(metaExpedientIds) ? metaExpedientIds.size() : 0) + ") time:  " + (System.currentTimeMillis() - t1) + " ms");
		
		long t2 = System.currentTimeMillis();
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				isAdminOrgan ? ExtendedPermission.ADMINISTRATION : permis));
		List<String> organCodis = organGestorRepository.findCodisByEntitatAndVigentIds(entitat, Utils.getNullIfEmpty(organIds));
		organCodis = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organCodis);
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis organIds (" + (Utils.isNotEmpty(organCodis) ? organCodis.size() : 0) + ") time:  " + (System.currentTimeMillis() - t2) + " ms");
		
		
		long t3 = System.currentTimeMillis();
		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				permis));
		// there is no need to find descendants because for the query to find procediments it doesn't matter  
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis metaExpedientOrganIds (" + (Utils.isNotEmpty(metaExpedientOrganIds) ? metaExpedientOrganIds.size() : 0) + ") time:  " + (System.currentTimeMillis() - t3) + " ms");
		
		long t4 = System.currentTimeMillis();
		// Cercam els òrgans amb permisos per procediemnts comuns
		List<Serializable> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(
				OrganGestorEntity.class,
				ExtendedPermission.COMU,
				permis);

		// Cercam els òrgans amb permisos de administracio comuns
		List<Long> organAdmIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				ExtendedPermission.ADM_COMU));
		boolean accessAllComu = false;
		if (Utils.isNotEmpty(organProcedimentsComunsIds) || Utils.isNotEmpty(organAdmIds)) {
			accessAllComu = true;
		}
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis organProcedimentsComunsIds (" + (Utils.isNotEmpty(organProcedimentsComunsIds) ? organProcedimentsComunsIds.size() : 0) + ") time:  " + (System.currentTimeMillis() - t4) + " ms");
		
//		// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
//		// in issue #1330 unnecessary ids were removed from the lists
//		// but if despite it there are still 1000+ values new solution must be implemented to not truncate lists.
//		if (Utils.isBiggerThan(metaExpedientIds, 1000)) {
//			logger.info("Truncating metaExpedientIds to 1000 to avoid ORA-01795");
//			metaExpedientIds = metaExpedientIds.subList(0, 1000);
//		}
//		if (Utils.isBiggerThan(organCodis, 1000)) {
//			logger.info("Truncating organIds to 1000 to avoid ORA-01795");
//			organCodis = organCodis.subList(0, 1000);
//		}
//		if (Utils.isBiggerThan(metaExpedientOrganIds, 1000)) {
//			logger.info("Truncating metaExpedientOrganIds to 1000 to avoid ORA-01795");
//			metaExpedientOrganIds = metaExpedientOrganIds.subList(0, 1000);
//		}
			
		long t5 = System.currentTimeMillis();
		MetaExpedientFiltre filtre = MetaExpedientFiltre.builder()
				.entitat(entitat)
				.actiu(nomesActius)
				.filtre(filtreNomOrCodiSia)
				.esAdminEntitat(isAdminEntitat)
				.esAdminOrgan(isAdminOrgan)
				.metaExpedientIdPermesos(metaExpedientIds)
				.organCodiPermesos(organCodis)
				.metaExpedientOrganIdPermesos(metaExpedientOrganIds)
				.revisioActiva(isRevisioActiva())
				.organGestorIComu(comu && organId != null)
				.organ(organId != null ? organGestorRepository.getOne(organId) : null)
				.allComuns(accessAllComu)
				.build();
//		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndActiuAndFiltreAndPermes(filtre); --> Ho deixam preparat per quan passem a jboss7
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndActiuAndFiltreAndPermes(
				filtre.getEntitat(),
				filtre.isEsNullActiu(),
				filtre.getActiu(),
				filtre.isEsNullFiltre(),
				filtre.getFiltre(),
				filtre.isEsAdminEntitat(),
//				filtre.isEsAdminOrgan(),
				filtre.isEsNullMetaExpedientIdPermesos(),
				filtre.getMetaExpedientIdPermesos(0),
				filtre.getMetaExpedientIdPermesos(1),
				filtre.getMetaExpedientIdPermesos(2),
				filtre.getMetaExpedientIdPermesos(3),
				filtre.isEsNullOrganCodiPermesos(),
				filtre.getOrganCodiPermesos(0),
				filtre.getOrganCodiPermesos(1),
				filtre.getOrganCodiPermesos(2),
				filtre.getOrganCodiPermesos(3),
				filtre.isEsNullMetaExpedientOrganIdPermesos(),
				filtre.getMetaExpedientOrganIdPermesos(0),
				filtre.getMetaExpedientOrganIdPermesos(1),
				filtre.getMetaExpedientOrganIdPermesos(2),
				filtre.getMetaExpedientOrganIdPermesos(3),
				filtre.isRevisioActiva(),
				filtre.isOrganGestorIComu(),
				filtre.getOrgan(),
				filtre.isAllComuns()
				);
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis findByEntitatAndActiuAndFiltreAndPermes (" + (Utils.isNotEmpty(organProcedimentsComunsIds) ? organProcedimentsComunsIds.size() : 0) + ") time:  " + (System.currentTimeMillis() - t5) + " ms");
		
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientHelper.findAmbPermis end:  " + (System.currentTimeMillis() - t0) + " ms");
		
		return metaExpedients;
	}

	public MetaExpedientTascaEntity tascaUpdate(
			MetaExpedientTascaEntity metaExpTascaEntity,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException {
		metaExpTascaEntity.update(
				metaExpedientTasca.getCodi(),
				metaExpedientTasca.getNom(),
				metaExpedientTasca.getDescripcio(),
				metaExpedientTasca.getResponsable(),
				metaExpedientTasca.getDataLimit(),
				metaExpedientTasca.getDuracio(),
				metaExpedientTasca.getPrioritat(),
				metaExpedientTasca.getEstatIdCrearTasca()==null?null:expedientEstatRepository.getOne(metaExpedientTasca.getEstatIdCrearTasca()),
				metaExpedientTasca.getEstatIdFinalitzarTasca()==null?null:expedientEstatRepository.getOne(metaExpedientTasca.getEstatIdFinalitzarTasca()));
		return metaExpTascaEntity;
	}

	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca,
			String rolActual,
			Long organId) throws NotFoundException {
		logger.debug(
				"Creant una nova tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "metaExpedientTasca=" + metaExpedientTasca + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		Long idEstatCrear = metaExpedientTasca.getEstatIdCrearTasca();
		ExpedientEstatEntity estatCrearTasca = idEstatCrear != null ? expedientEstatRepository.getOne(idEstatCrear) : null;
		Long idEstatFinalitzar = metaExpedientTasca.getEstatIdFinalitzarTasca();
		ExpedientEstatEntity estatFinalitzarTasca = idEstatFinalitzar != null ? expedientEstatRepository.getOne(
				idEstatFinalitzar) : null;
		MetaExpedientTascaEntity entity = MetaExpedientTascaEntity.getBuilder(
				metaExpedientTasca.getCodi(),
				metaExpedientTasca.getNom(),
				metaExpedientTasca.getDescripcio(),
				metaExpedientTasca.getResponsable(),
				metaExpedient,
				metaExpedientTasca.getDataLimit(),
				metaExpedientTasca.getDuracio(),
				metaExpedientTasca.getPrioritat(),
				estatCrearTasca,
				estatFinalitzarTasca).build();
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(metaExpedientTascaRepository.save(entity), MetaExpedientTascaDto.class);
	}
	
	public MetaExpedientTascaEntity findTascaByMetaExpedientAndCodi(MetaExpedientEntity metaExpedient, String codi) {
		return metaExpedientTascaRepository.findByMetaExpedientAndCodi(metaExpedient, codi);
	}

	public void canviarRevisioAPendentEnviarEmail(Long entitatId, Long metaExpedientId, Long organId) {

		boolean revisioActiva = configHelper.getAsBoolean("es.caib.ripea.metaexpedients.revisio.activa");
		
		if (revisioActiva) {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
			MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarAccesMetaExpedient(entitat, metaExpedientId, organId, false);

			if (metaExpedientEntity.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.PENDENT) {
				metaExpedientEntity.updateRevisioEstat(
						MetaExpedientRevisioEstatEnumDto.PENDENT);
				
				emailHelper.canviEstatRevisioMetaExpedient(metaExpedientEntity, entitatId);
			}
		}
	}
	
	public void canviarRevisioADisseny(Long entitatId, Long metaExpedientId, Long organId) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarAccesMetaExpedient(entitat, metaExpedientId, organId, false);

		if (metaExpedientEntity.getRevisioEstat() != MetaExpedientRevisioEstatEnumDto.DISSENY) {
			metaExpedientEntity.updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.DISSENY);
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

		List<MetaExpedientEntity> procedimentsDeOrganIDeDescendentsDeOrgan = new ArrayList<>();

		OrganGestorEntity organ = organGestorRepository.getOne(organId);
		List<String> codisOrgansDescendents = organGestorHelper.findCodisDescendents(organ.getEntitat().getCodi(), organId);

		// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
		List<List<String>> sublists = org.apache.commons.collections4.ListUtils.partition(codisOrgansDescendents, 1000);

		for (List<String> sublist : sublists) {
			procedimentsDeOrganIDeDescendentsDeOrgan.addAll(metaExpedientRepository.findByOrganGestorCodis(organ.getEntitat(), sublist));
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
	
	public List<Long> getIdsReadPermesos(Long entitatId) {
		List<Long> readPermIds = getIds(
				findAmbPermis(
					entitatId,
					ExtendedPermission.READ,
					true,
					null,
					false,
					false,
					null, 
					false));
		return readPermIds;
		
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
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(metaExpedientId);
		metaExpedient.updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PENDENT);

		try {

			CrearReglaResponseDto rearReglaResponseDto = distribucioReglaHelper.crearRegla(
					metaExpedient.getEntitat().getUnitatArrel(),
					metaExpedient.getClassificacio());

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
	public void actualitzarProcediments(
			EntitatEntity entitat,
			List<MetaExpedientEntity> metaExpedients,
			Locale locale,
			ProgresActualitzacioDto progresActualitzacioDto) {
		
		ProgresActualitzacioDto progres = null;
		if (progresActualitzacioDto != null) {
			progres = progresActualitzacioDto;
		} else {
			progres = progresActualitzacio.get(entitat.getCodi());
			if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
				logger.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
				return;
			}
			// inicialitza el seguiment del prgrés d'actualització
			progres = new ProgresActualitzacioDto();
			progresActualitzacio.put(entitat.getCodi(), progres);
		}
		
		Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
		try {
			// remove procediments without codi sia
			Iterator<MetaExpedientEntity> it = metaExpedients.iterator();
			while (it.hasNext()) {
				MetaExpedientEntity metaExpedient = it.next();
				if (metaExpedient.getTipusClassificacio() == TipusClassificacioEnumDto.ID) {
					it.remove();
				}
			}

			progres.setNumOperacions(metaExpedients.size());
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("procediment.synchronize.titol.inici")).infoText(msg("procediment.synchronize.info.inici", metaExpedients.size())).build());

			Integer organsNoSincronitzats = 0;
			Integer modificats = 0;
			Integer fallat = 0;
			for(MetaExpedientEntity metaExpedient: metaExpedients) {
				ActualitzacioInfo.ActualitzacioInfoBuilder infoBuilder = ActualitzacioInfo.builder()
						.codiSia(metaExpedient.getClassificacio())
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
							metaExpedient.getClassificacio());
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
					infoBuilder.errorText(msg("procediment.synchronize.error.exist", metaExpedient.getClassificacio()));
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

			actualitzaAvisosSyncProcediments(avisosProcedimentsOrgans, entitat.getId());

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
	
	
	public MetaExpedientDto toMetaExpedientDto(
			MetaExpedientEntity metaExpedient, 
			Long adminOrganId) {
		MetaExpedientDto metaExpedientDto = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
		
		metaNodeHelper.omplirMetaDadesPerMetaNode(metaExpedientDto);
		omplirMetaDocumentsPerMetaExpedient(metaExpedient, metaExpedientDto);
		metaNodeHelper.omplirPermisosPerMetaNodes(Arrays.asList(metaExpedientDto), true);
			
		metaExpedientDto.setExpedientEstatsCount(expedientEstatRepository.countByMetaExpedient(metaExpedient));
		metaExpedientDto.setExpedientTasquesCount(
				metaExpedientTascaRepository.countByMetaExpedient(metaExpedient));
		
		List<GrupEntity> grups = metaExpedient.getGrups();
//		if (adminOrganId != null) {
//			for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
//				GrupEntity grup = iter.next();
//				if (grup.getOrganGestor() == null || !organGestorHelper.findParesIds(grup.getOrganGestor().getId(), true).contains(adminOrganId)) {
//					iter.remove();
//				}
//			}
//		}
		metaExpedientDto.setGrupsCount(grups.size());
		
		metaExpedientDto.setNumComentaris(metaExpedient.getComentaris().size());
		if (metaExpedient.getOrganGestor() != null) {
			metaExpedientDto.setOrganEstat(metaExpedient.getOrganGestor().getEstat());
			metaExpedientDto.setOrganTipusTransicio(metaExpedient.getOrganGestor().getTipusTransicio());
		}
			
		return metaExpedientDto;
	}


	public void omplirMetaDocumentsPerMetaExpedient(MetaExpedientEntity metaExpedient, MetaExpedientDto dto) {
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		for (MetaDocumentEntity metaDocument : metaDocumentsDelMetaExpedient) {
			metaDocuments.add(conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class));
		}
		dto.setMetaDocuments(metaDocuments);
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
			avisRepository.deleteAll(avisosSinc);
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
