package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPublicacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPublicacioRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
import es.caib.ripea.core.repository.NodeRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private MetaNodeRepository metaNodeRepository;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	@Autowired
	private NodeRepository nodeRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private DocumentPublicacioRepository documentPublicacioRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private PermisosHelper permisosHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Resource
	private RolHelper rolHelper;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;
    @Autowired
    private OrganGestorCacheHelper organGestorCacheHelper;

	public EntitatEntity comprovarEntitat(
			String entitatCodi,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdmin,
			boolean comprovarPermisUsuariOrAdmin) throws NotFoundException {
		EntitatEntity entitat = entitatRepository.findByUnitatArrel(entitatCodi);
		if (entitat == null) {
			throw new NotFoundException(entitatCodi, EntitatEntity.class);
		}
		return comprovarEntitat(
				entitat.getId(),
				comprovarPermisUsuari,
				comprovarPermisAdmin,
				comprovarPermisUsuariOrAdmin, false, false);
	}

	public EntitatEntity comprovarEntitatPerMetaExpedients(Long entitatId) {
		return comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
	}
	
	public EntitatEntity comprovarEntitat(
			Long entitatId) throws NotFoundException {
		
		return comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				false,
				false);
		
	}
	
	
	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdmin,
			boolean comprovarPermisUsuariOrAdmin, 
			boolean comprovarPermisUsuariOrAdminOrOrgan, 
			boolean comprovarPermisAdminOrOrgan) throws NotFoundException {
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			throw new NotFoundException(entitatId, EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisUsuari) {
			boolean esLectorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
			        new Permission[] { ExtendedPermission.READ }, auth);
			if (!esLectorEntitat) {
				throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(), "READ");
			}
		}
		if (comprovarPermisAdmin) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
			        new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
			if (!esAdministradorEntitat) {
				throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(),
				        "ADMINISTRATION");
			}
		}
		if (comprovarPermisUsuariOrAdmin) {
			boolean esAdministradorOLectorEntitat = permisosHelper.isGrantedAny(entitatId,
			        EntitatEntity.class,
			        new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.READ }, auth);
			if (!esAdministradorOLectorEntitat) {
				throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(),
				        "ADMINISTRATION || READ");
			}
		}
		if (comprovarPermisUsuariOrAdminOrOrgan) {
			boolean esAdministradorOLectorEntitat = permisosHelper.isGrantedAny(entitatId,
			        EntitatEntity.class,
			        new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.READ }, auth);
			List<OrganGestorEntity> organs = organGestorHelper.findAmbEntitatPermis(
					entitat,
					ExtendedPermission.ADMINISTRATION);
			if (!esAdministradorOLectorEntitat && (organs == null || organs.isEmpty())) {
				throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(),
				        "ADMINISTRATION || READ || ORGAN");
			}
		}
		
		if (comprovarPermisAdminOrOrgan) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAny(entitatId,
			        EntitatEntity.class,
			        new Permission[] { ExtendedPermission.ADMINISTRATION}, auth);
			List<OrganGestorEntity> organs = organGestorHelper.findAmbEntitatPermis(
					entitat,
					ExtendedPermission.ADMINISTRATION);
			if (!esAdministradorEntitat && (organs == null || organs.isEmpty())) {
				throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(),
				        "ADMINISTRATION || READ || ORGAN");
			}
		}
		return entitat;
	}
	
	public OrganGestorEntity comprovarOrganGestorAdmin(Long entitatId, Long id) {
		OrganGestorEntity organGestor = organGestorRepository.findOne(id);
		if (organGestor == null) {
			throw new NotFoundException(id, OrganGestorEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
		        new Permission[] { ExtendedPermission.ADMINISTRATION },
		        auth);
		if (!esAdministradorEntitat) {
			boolean esAdministradorOrganGestor = organGestorHelper.isOrganGestorPermes(
					organGestor,
					ExtendedPermission.ADMINISTRATION);
			if (!esAdministradorOrganGestor) {
				throw new PermissionDeniedException(id, OrganGestorEntity.class, auth.getName(),
				        "ADMINISTRATION");
			}	
		}
		return organGestor;
	}
	

//	public OrganGestorEntity comprovarOrganGestorPerRolUsuari(
//			EntitatEntity entitat,
//			Long id) {
//		OrganGestorEntity organGestor = organGestorRepository.findOne(id);
//		if (organGestor == null) {
//			throw new NotFoundException(id, OrganGestorEntity.class);
//		}
//		if (!entitat.equals(organGestor.getEntitat())) {
//			throw new ValidationException(
//					id,
//					MetaNodeEntity.class,
//					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'òrgan gestor");
//		}
//
//		// Cercam els metaExpedients amb permisos assignats directament
//		List<Long> metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
//				MetaNodeEntity.class,
//				ExtendedPermission.READ));
//		List<Long> metaExpedientIdPermesosPerEntitat = null;
//		if (metaExpedientIdPermesos != null && !metaExpedientIdPermesos.isEmpty()) {
//			metaExpedientIdPermesosPerEntitat = metaExpedientRepository.findIdsByEntitat(entitat, metaExpedientIdPermesos);
//		}
//
//		if (metaExpedientIdPermesosPerEntitat != null && !metaExpedientIdPermesosPerEntitat.isEmpty()) {
//			//if user has assigned direct permissions for any metaexpedient of entitat, then he has permissions for all organs of this entitat
//		} else {
//			boolean existsInPermitted = false;
//			List<OrganGestorEntity> organGestors = getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(entitat);
//
//			for (OrganGestorEntity organGestorEntity : organGestors) {
//				if (organGestorEntity.getId().equals(id)) {
//					existsInPermitted = true;
//				}
//			}
//
//			if (!existsInPermitted) {
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//				throw new SecurityException(
//						"Sense permisos de consulta sobre l'òrgan gestor (" +
//						"id=" + id + ", " +
//				        "usuari=" + auth.getName() + ")");
//			}
//
//		}
//
//		return organGestor;
//	}
	
	
	
	
	
	public List<OrganGestorEntity> getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(EntitatEntity entitat) {

		Set<String> organCodis = new HashSet<>();
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				ExtendedPermission.READ));
//		organGestorHelper.afegirOrganGestorFillsIds(entitat, organIdPermesos);
		organCodis.addAll(organGestorRepository.findCodisByIdList(organIdPermesos));

		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				ExtendedPermission.READ));
		if (metaExpedientOrganIdPermesos != null && !metaExpedientOrganIdPermesos.isEmpty()) {
			organCodis.addAll(metaExpedientOrganGestorRepository.findOrganGestorCodisByMetaExpedientOrganGestorIds(metaExpedientOrganIdPermesos));
//			organGestorHelper.afegirOrganGestorFillsIds(entitat, organsIdsPerMetaExpedientOrganIdPermesos);
//			organIdPermesos.addAll(organsIdsPerMetaExpedientOrganIdPermesos);
		}

		List<String> organsGestorsPermesos = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), new ArrayList<>(organCodis));

		List<OrganGestorEntity> organGestors = new ArrayList<>();
		// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
		List<List<String>> sublists = org.apache.commons.collections4.ListUtils.partition(organsGestorsPermesos, 1000);

		for (List<String> sublist : sublists) {
			organGestors.addAll(organGestorRepository.findByEntitatAndCodis(entitat, sublist));
		}

	    return organGestors;
	}
	
	

	public MetaNodeEntity comprovarMetaNode(EntitatEntity entitat, Long id) {
		MetaNodeEntity metaNode = metaNodeRepository.findOne(id);
		if (metaNode == null) {
			throw new NotFoundException(id, MetaNodeEntity.class);
		}
		if (!entitat.equals(metaNode.getEntitat())) {
			throw new ValidationException(id, MetaNodeEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del meta-node");
		}
		return metaNode;
	}
	
	
	


	public MetaExpedientEntity comprovarMetaExpedient(EntitatEntity entitat, Long metaExpedientId) {
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		if (metaExpedient == null) {
			throw new NotFoundException(metaExpedientId, MetaExpedientEntity.class);
		}
		if (HibernateHelper.isProxy(entitat)) {
			entitat = HibernateHelper.deproxy(entitat);
		}
		if (HibernateHelper.isProxy(metaExpedient)) {
			metaExpedient = HibernateHelper.deproxy(metaExpedient);
		}
		EntitatEntity metaExpedientEntitat = metaExpedient.getEntitat();
		if (HibernateHelper.isProxy(metaExpedientEntitat)) {
			metaExpedientEntitat = HibernateHelper.deproxy(metaExpedient.getEntitat());
		}
		if (!entitat.equals(metaExpedientEntitat)) {
			throw new ValidationException(metaExpedientId, MetaExpedientEntity.class,
			        "L'entitat especificada (id=" + entitat.getId()
			                + ") no coincideix amb l'entitat del meta-expedient");
		}
		return metaExpedient;
	}
	
	
	public MetaExpedientEntity comprovarMetaExpedient(Long metaExpedientId) {
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		if (metaExpedient == null) {
			throw new NotFoundException(metaExpedientId, MetaExpedientEntity.class);
		}
		if (HibernateHelper.isProxy(metaExpedient)) {
			metaExpedient = HibernateHelper.deproxy(metaExpedient);
		}
		EntitatEntity metaExpedientEntitat = metaExpedient.getEntitat();
		if (HibernateHelper.isProxy(metaExpedientEntitat)) {
			metaExpedientEntitat = HibernateHelper.deproxy(metaExpedient.getEntitat());
		}
		return metaExpedient;
	}

	public MetaExpedientEntity comprovarMetaExpedient(
			EntitatEntity entitat,
			Long metaExpedientId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin, 
			String rolActual, 
			Long organId) {
		MetaExpedientEntity metaExpedient = comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		if (comprovarPermisCreate) {
			if (!metaExpedient.isActiu()) {
				throw new ValidationException(metaExpedientId, MetaExpedientEntity.class, "El procediment no es troba actiu (id=" + metaExpedientId + ")");
			}
		}
		
		if (comprovarPermisRead) {
			comprovarPermisMetaExpedient(
					metaExpedientId,
					ExtendedPermission.READ,
					"READ",
					rolActual);
		}
		if (comprovarPermisWrite) {
			comprovarPermisMetaExpedient(
					metaExpedientId,
					ExtendedPermission.WRITE,
					"WRITE",
					rolActual);
		}
		if (comprovarPermisCreate) {
			comprovarPermisMetaExpedient(
				metaExpedientId,
				ExtendedPermission.CREATE,
				"CREATE",
				rolActual);
		}
		if (comprovarPermisDelete) {
			comprovarPermisMetaExpedient(
					metaExpedientId,
					ExtendedPermission.DELETE,
					"DELETE",
					rolActual);
		}
		
		return metaExpedient;
	}

	public MetaExpedientEntity comprovarMetaExpedientAdmin(
			EntitatEntity entitat,
			Long id, 
			Long organId) {
		MetaExpedientEntity metaExpedient = comprovarMetaExpedient(entitat, id);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitat.getId(),
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (esAdministradorEntitat) {
			return metaExpedient;
		}
		if (metaExpedient.getOrganGestor() == null) {
			if (organId == null) {
				throw new ValidationException(id, MetaExpedientEntity.class, "El meta-expedient no té cap organ gestor asociat (id=" + id + ")");
			} else {
				boolean hasPermisAdminComu = permisosHelper.isGrantedAll(
						organId,
						OrganGestorEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADM_COMU },
						auth);
				if (!hasPermisAdminComu) {
					throw new PermissionDeniedException(id, OrganGestorEntity.class, auth.getName(),
					        "ADM_COMU");
				}
			}
		} else {
			// si no es administrador d'entitat comprovar si es administrador del seu organ gestor
			comprovarOrganGestorAdmin(
					entitat.getId(),
					metaExpedient.getOrganGestor().getId());
		}
		return metaExpedient;
	}
	
	public MetaDocumentEntity comprovarMetaDocument(
			Long entitatId,
			Long metaDocumentId) {

		return comprovarMetaDocument(
				metaDocumentId);
	}

	public MetaDocumentEntity comprovarMetaDocument(Long metaDocumentId) {
		MetaDocumentEntity metaDocument = metaDocumentRepository.findOne(metaDocumentId);
		if (metaDocument == null) {
			throw new NotFoundException(metaDocumentId, MetaDocumentEntity.class);
		}
		if (HibernateHelper.isProxy(metaDocument)) {
			metaDocument = HibernateHelper.deproxy(metaDocument);
		}
		EntitatEntity metaDocumentEntitat = metaDocument.getEntitat();
		if (HibernateHelper.isProxy(metaDocumentEntitat)) {
			metaDocumentEntitat = HibernateHelper.deproxy(metaDocumentEntitat);
		}

		if (metaDocument.getMetaExpedient() != null) {
			comprovarMetaExpedient(metaDocument.getMetaExpedient().getId());
		}

		return metaDocument;
	}

	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id) {
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovant metadDocument-metaExpedient (" +
					"entitatId=" + entitat.getId() + ", " +
					"metaExpedientId=" + (metaExpedient != null ? metaExpedient.getId() : "") + ", " +
					"metaDocumentId=" + id + ")");
		MetaDocumentEntity metaDocument = metaDocumentRepository.findOne(id);
		if (metaDocument == null) {
			throw new NotFoundException(id, MetaDocumentEntity.class);
		}
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovant metadDocument-metaExpedient. Obtingut metaDocument (" +
					"metaDocumentNom=" + metaDocument.getNom() + ", " +
					"metaDocumentId=" + metaDocument.getId() + ", " +
					"metaDocument.metaExpedientId=" + metaDocument.getMetaExpedient().getId() + ")");

		EntitatEntity metaDocumentEntitat = metaDocument.getEntitat();
		if (HibernateHelper.isProxy(metaDocumentEntitat)) {
			metaDocumentEntitat = HibernateHelper.deproxy(metaDocumentEntitat);
		}
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovant metadDocument-metaExpedient. Obtinguda entitat del metaDocument (" +
					"metaDocumentEntitatNom=" + metaDocumentEntitat.getNom() + ", " +
					"metaDocumentEntitatId=" + metaDocumentEntitat.getId() + ")");
		if (!entitat.equals(metaDocumentEntitat)) {
			throw new ValidationException(id, MetaDocumentEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del meta-document");
		}
		if (metaExpedient != null && !metaExpedient.getId().equals(metaDocument.getMetaExpedient().getId())) {
			throw new ValidationException(id, MetaDocumentEntity.class,
					"El meta-expedient especificat (id=" + metaExpedient.getId() + ") " +
					"no coincideix amb el meta-expedient del meta-document (" + metaDocument.getMetaExpedient().getId() + ")");
		}
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovat metadDocument-metaExpedient (" +
					"metaExpedientId=" + metaDocument.getMetaExpedient().getId() + ", " +
					"metaDocumentId=" + metaDocument.getId() + ")");
		return metaDocument;
	}

	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id,
			boolean comprovarActiu,
			boolean comprovarMetaExpedient) {
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovant metadDocument (" +
					"entitatId=" + entitat.getId() + ", " +
					"metaExpedientId=" + (metaExpedient != null ? metaExpedient.getId() : "null") + ", " +
					"metaDocumentId=" + id + ")");

		MetaDocumentEntity metaDocument;
		if (comprovarMetaExpedient) {
			metaDocument = comprovarMetaDocument(
					entitat,
					metaExpedient,
					id);
		} else {
			metaDocument = comprovarMetaDocument(
					id);
		}
		if (comprovarActiu) {
			if (!metaDocument.isActiu()) {
				throw new ValidationException(id, MetaDocumentEntity.class,
				        "El meta-document no es troba actiu (id=" + id + ")");
			}
		}
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Comprovat metadDocument correctament");
		return metaDocument;
	}

	public MetaDadaEntity comprovarMetaDada(EntitatEntity entitat, MetaNodeEntity metaNode, Long id) {
		MetaDadaEntity metaDada = metaDadaRepository.findOne(id);
		if (metaDada == null) {
			throw new NotFoundException(id, MetaDadaEntity.class);
		}
		if (!metaNode.equals(metaDada.getMetaNode())) {
			throw new ValidationException(id, MetaDadaEntity.class, "El meta-node especificat (id="
			        + metaNode.getId() + ") no coincideix amb el meta-node de la meta-dada");
		}
		if (!entitat.equals(metaDada.getMetaNode().getEntitat())) {
			throw new ValidationException(id, MetaExpedientEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del meta-expedient");
		}
		return metaDada;
	}
	
	
	public ContingutEntity comprovarContingut(Long id) {
		ContingutEntity contingut = contingutRepository.findOne(id);
		if (contingut == null) {
			throw new NotFoundException(id, ContingutEntity.class);
		}

		return contingut;
	}

	public NodeEntity comprovarNode(
			EntitatEntity entitat,
			Long nodeId) {
		NodeEntity node = nodeRepository.findOne(nodeId);
		if (node == null) {
			throw new NotFoundException(nodeId, NodeEntity.class);
		}

		return node;
	}

	public CarpetaEntity comprovarCarpeta(EntitatEntity entitat, Long id) {
		CarpetaEntity carpeta = carpetaRepository.findOne(id);
		if (carpeta == null) {
			throw new NotFoundException(id, CarpetaEntity.class);
		}

		return carpeta;
	}
	
	public ExpedientEntity comprovarExpedient(
			Long entitatId,
			Long expedientId) {
		
		return comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);
		
	}
	
	public boolean comprovarAdminEntitatOAdminOrganDelExpedient(ExpedientEntity expedient) {
		
		if (!rolHelper.doesCurrentUserHasRol("IPA_ADMIN") && !rolHelper.doesCurrentUserHasRol("IPA_ORGAN_ADMIN")) {
			return false;
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				expedient.getEntitat().getId(),
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		
		boolean esAdministradorOrgan = organGestorHelper.isOrganGestorPermes(
				expedient.getOrganGestor(),
				ExtendedPermission.ADMINISTRATION);
		
		if (esAdministradorEntitat || esAdministradorOrgan) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean comprovarRolActualAdminEntitatOAdminOrganDelExpedient(ExpedientEntity expedient, String rolActual) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (rolActual.equals("IPA_ADMIN")) {
			
			return permisosHelper.isGrantedAll(
					expedient.getEntitat().getId(),
					EntitatEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					auth);
		} else if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			
			return organGestorHelper.isOrganGestorPermes(
					expedient.getOrganGestor(),
					ExtendedPermission.ADMINISTRATION);
		} else {
			
			return false;
		}
	}
	
	public boolean comprovarSiRolTePermisPerModificarExpedient(ExpedientEntity expedient, String rolActual) {
		
		boolean rolActualAdminEntitatOAdminOrgan = comprovarRolActualAdminEntitatOAdminOrganDelExpedient(expedient, rolActual);
		if (rolActualAdminEntitatOAdminOrgan) {
			return true;
		} else {
			return comprovarPermisExpedient(
					expedient.getId(),
					ExtendedPermission.WRITE,
					"WRITE",
					false);
		}
	}
	

	
	public boolean comprovarSiEsPotModificarExpedient(ExpedientEntity expedient) {

		boolean expedientAgafatPerUsuariActual = comprovarSiExpedientAgafatPerUsuariActual(expedient);

		boolean usuariActualWrite = comprovarPermisExpedient(
				expedient.getId(),
				ExtendedPermission.WRITE,
				"WRITE",
				false);
		
		boolean permisosAdminEntitatOAdminOrgan = comprovarAdminEntitatOAdminOrganDelExpedient(expedient);
		
		if (((expedientAgafatPerUsuariActual && usuariActualWrite) || permisosAdminEntitatOAdminOrgan) && expedient.getEstat() == ExpedientEstatEnumDto.OBERT) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean comprovarSiEsPotReobrirExpedient(ExpedientEntity expedient) {

		boolean isReobrirPermes = configHelper.getAsBoolean("es.caib.ripea.expedient.permetre.reobrir");
		
		boolean expedientTancat = expedient.getEstat() == ExpedientEstatEnumDto.TANCAT;
		
		boolean isTancamentLogicActiu = configHelper.getAsBoolean("es.caib.ripea.expedient.tancament.logic");
		
		return isReobrirPermes && expedientTancat && (!isTancamentLogicActiu || (isTancamentLogicActiu && expedient.getTancatData() == null));		

	}
	
	public boolean comprovarSiExpedientAgafatPerUsuariActual(ExpedientEntity expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		return expedient.getAgafatPer() != null ? expedient.getAgafatPer().getCodi().equals(auth.getName()) : false;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExpedientEntity comprovarExpedientNewTransaction(
			Long expedientId,
			boolean comprovarAgafatPerUsuariActual,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete,
			String rolActual) {
		return comprovarExpedient(
				expedientId, 
				comprovarAgafatPerUsuariActual, 
				comprovarPermisRead, 
				comprovarPermisWrite, 
				comprovarPermisCreate, 
				comprovarPermisDelete, 
				rolActual);
	}

	public ExpedientEntity comprovarExpedientPermisRead(Long expedientId) {
		return comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
	}
	public ExpedientEntity comprovarExpedientPermisWrite(Long expedientId) {
		return comprovarExpedient(
				expedientId,
				false,
				false,
				true,
				false,
				false,
				null);
	}
	public ExpedientEntity comprovarExpedientPermisCreate(Long expedientId) {
		return comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				true,
				false,
				null);
	}
	public ExpedientEntity comprovarExpedientPermisDelete(Long expedientId) {
		return comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				true,
				null);
	}

	public ExpedientEntity comprovarExpedient(
			Long expedientId,
			boolean comprovarAgafatPerUsuariActual,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete,
			String rolActual) {

		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		if (expedient == null) {
			throw new NotFoundException(expedientId, ExpedientEntity.class);
		}
		if (expedient.getEsborrat() != 0) {
			throw new NotFoundException(expedientId, ExpedientEntity.class);
		}
		comprovarEntitat(expedient.getEntitat().getId(), false, false, false, true, false);

		if (comprovarAgafatPerUsuariActual && !RolHelper.isAdminEntitat(rolActual) && !RolHelper.isAdminOrgan(rolActual)) {
			UsuariEntity agafatPer = expedient.getAgafatPer();
			if (agafatPer != null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(agafatPer.getCodi())) {
					throw new ValidationException(expedientId, ContingutEntity.class,
					        "L'expedient no està agafat per l'usuari actual (" + "usuariActualCodi="
					                + auth.getName() + ")");
				}
			} else {
				throw new ValidationException(expedientId, ContingutEntity.class,
				        "L'expedient no està agafat per cap usuari");
			}
		}
		
		// comprovar si l'expedient està relacionat i és una consulta de LECTURA sobre algún element de l'expedient, llavors no mirar permisos
		boolean comprovarNomesLectura = (!comprovarPermisWrite && !comprovarPermisCreate && !comprovarPermisDelete);
		if (comprovarNomesLectura) {
			boolean relacionatAmbAlgunExpedient = expedient.getRelacionatsAmb() != null && !expedient.getRelacionatsAmb().isEmpty();
			boolean relacionatPerAlgunExpedient = expedient.getRelacionatsPer() != null && !expedient.getRelacionatsPer().isEmpty();
			if (relacionatAmbAlgunExpedient || relacionatPerAlgunExpedient) {
				comprovarPermisRead = false;
			}
		}
		

		if (comprovarPermisRead) {
			comprovarPermisExpedient(
					expedientId,
					ExtendedPermission.READ,
					"READ", true);
		}
		if (comprovarPermisWrite) {
			comprovarPermisExpedient(
					expedientId,
					ExtendedPermission.WRITE,
					"WRITE", true);
		}
		if (comprovarPermisCreate) {
		comprovarPermisExpedient(
				expedientId,
				ExtendedPermission.CREATE,
				"CREATE", true);
		}
		if (comprovarPermisDelete) {
			comprovarPermisExpedient(
					expedientId,
					ExtendedPermission.DELETE,
					"DELETE", true);
		}
		
		return expedient;
	}
	
	public void comprovarEstatExpedient(
			Long entitatId,
			Long expedientId,
			ExpedientEstatEnumDto estat) {
		EntitatEntity entitat = comprovarEntitat(entitatId, false, false, false, true, false);
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		if (expedient == null) {
			throw new NotFoundException(expedientId, ExpedientEntity.class);
		}
		if (expedient.getEsborrat() != 0) {
			throw new NotFoundException(expedientId, ExpedientEntity.class);
		}
		if (!entitat.getId().equals(expedient.getEntitat().getId())) {
			throw new ValidationException(expedientId, ExpedientEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat de l'expedient");
		}
		if (estat.equals(ExpedientEstatEnumDto.OBERT) && !expedient.getEstat().equals(estat)) {
			throw new ValidationException(expedientId, ContingutEntity.class, "L'expedient no està obert");
		}
		if (estat.equals(ExpedientEstatEnumDto.TANCAT) && !expedient.getEstat().equals(estat)) {
			throw new ValidationException(expedientId, ContingutEntity.class, "L'expedient no està tancat");
		}
	}



	/**
	 * checking if expedient estat has modify permissions
	 * 
	 * @param metaExpedientId
	 * @return
	 */
	public boolean hasMetaExpedientWritePermissons(Long metaExpedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return permisosHelper.isGrantedAll(metaExpedientId, MetaNodeEntity.class,
		        new Permission[] { ExtendedPermission.WRITE }, auth);
	}

	public DocumentEntity comprovarDocument(EntitatEntity entitat, ExpedientEntity expedient, Long documentId,
	                                        boolean comprovarPermisRead, boolean comprovarPermisWrite,
	                                        boolean comprovarPermisCreate, boolean comprovarPermisDelete) {
		DocumentEntity document = documentRepository.findOne(documentId);
		if (document == null) {
			throw new NotFoundException(documentId, DocumentEntity.class);
		}
		if (!document.getEntitat().equals(entitat)) {
			throw new ValidationException(documentId, DocumentEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del document");
		}
		if (expedient != null && !document.getExpedient().equals(expedient)) {
			throw new ValidationException(documentId, DocumentEntity.class,
			        "L'expedient especificat (id=" + expedient.getId()
			                + ") no coincideix amb l'expedient del document (id="
			                + document.getExpedient().getId() + ")");
		}
		if (document.getMetaDocument() != null
		        && (comprovarPermisRead || comprovarPermisWrite || comprovarPermisDelete)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<Permission> permisos = new ArrayList<Permission>();
			StringBuilder permisosStr = new StringBuilder();
			if (comprovarPermisRead) {
				permisos.add(ExtendedPermission.READ);
				if (permisosStr.length() > 0)
					permisosStr.append(" && ");
				permisosStr.append("READ");
			}
			if (comprovarPermisWrite) {
				permisos.add(ExtendedPermission.WRITE);
				if (permisosStr.length() > 0)
					permisosStr.append(" && ");
				permisosStr.append("WRITE");
			}
			if (comprovarPermisDelete) {
				permisos.add(ExtendedPermission.DELETE);
				if (permisosStr.length() > 0)
					permisosStr.append(" && ");
				permisosStr.append("DELETE");
			}
			boolean granted = permisosHelper.isGrantedAll(document.getMetaDocument().getId(),
			        MetaNodeEntity.class, permisos.toArray(new Permission[permisos.size()]), auth);
			if (!granted) {
				throw new PermissionDeniedException(documentId, DocumentEntity.class, auth.getName(),
				        permisosStr.toString());
			}
		}
		return document;
	}

	public DadaEntity comprovarDada(NodeEntity node, Long dadaId) {
		DadaEntity dada = dadaRepository.findOne(dadaId);
		if (dada == null) {
			throw new NotFoundException(dadaId, DadaEntity.class);
		}
		if (!dada.getNode().equals(node)) {
			throw new ValidationException(dadaId, DadaEntity.class,
			        "El node especificat (id=" + node.getId() + ") no coincideix amb el node de la dada");
		}
		return dada;
	}

	/*
	 * public RegistreEntity comprovarRegistre( Long id, BustiaEntity bustiaPare) {
	 * RegistreEntity registre = registreRepository.findOne(id); if (registre ==
	 * null) { throw new NotFoundException( id, RegistreEntity.class); } if
	 * (bustiaPare != null) { if (registre.getPare() != null) { if
	 * (!registre.getPare().getId().equals(bustiaPare.getId())) { throw new
	 * ValidationException( id, RegistreEntity.class, "La bústia especificada (id="
	 * + bustiaPare.getId() +
	 * ") no coincideix amb la bústia de l'anotació de registre"); } } } return
	 * registre; }
	 */

	public InteressatEntity comprovarInteressat(ExpedientEntity expedient, Long interessatId) {
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NotFoundException(interessatId, InteressatEntity.class);
		}
		if (HibernateHelper.isProxy(expedient))
			expedient = HibernateHelper.deproxy(expedient);
		ExpedientEntity expedientInteressat = interessat.getExpedient();
		if (HibernateHelper.isProxy(expedientInteressat))
			expedientInteressat = HibernateHelper.deproxy(expedientInteressat);
		if (expedient != null && !expedientInteressat.equals(expedient)) {
			throw new ValidationException(interessatId, InteressatEntity.class,
			        "L'expedient especificat (id=" + expedient.getId()
			                + ") no coincideix amb l'expedeint de l'interessat (id="
			                + interessat.getExpedient().getId() + ")");
		}
		return interessat;
	}

	public DocumentNotificacioEntity comprovarNotificacio(ExpedientEntity expedient, DocumentEntity document,
	                                                      Long notificacioId) {
		DocumentNotificacioEntity notificacio = documentNotificacioRepository.findOne(notificacioId);
		if (notificacio == null) {
			throw new NotFoundException(notificacioId, DocumentNotificacioEntity.class);
		}
		if (!notificacio.getExpedient().equals(expedient)) {
			throw new ValidationException(notificacioId, DocumentNotificacioEntity.class,
			        "L'expedient especificat (id=" + expedient.getId()
			                + ") no coincideix amb l'expedient de la notificació (id="
			                + notificacio.getExpedient().getId() + ")");
		}
		if (document != null && !notificacio.getDocument().equals(document)) {
			throw new ValidationException(notificacioId, DocumentNotificacioEntity.class,
			        "El document especificat (id=" + document.getId()
			                + ") no coincideix amb el document de la notificació (id="
			                + notificacio.getDocument().getId() + ")");
		}
		return notificacio;
	}

	public DocumentPublicacioEntity comprovarPublicacio(ExpedientEntity expedient, DocumentEntity document,
	                                                    Long publicacioId) {
		DocumentPublicacioEntity publicacio = documentPublicacioRepository.findOne(publicacioId);
		if (publicacio == null) {
			throw new NotFoundException(publicacioId, DocumentNotificacioEntity.class);
		}
		if (!publicacio.getExpedient().equals(expedient)) {
			throw new ValidationException(publicacioId, DocumentPublicacioEntity.class,
			        "L'expedient especificat (id=" + expedient.getId()
			                + ") no coincideix amb l'expedient de la publicació (id="
			                + publicacio.getExpedient().getId() + ")");
		}
		if (document != null && !publicacio.getDocument().equals(document)) {
			throw new ValidationException(publicacioId, DocumentPublicacioEntity.class,
			        "El document especificat (id=" + document.getId()
			                + ") no coincideix amb el document de la publicació (id="
			                + publicacio.getDocument().getId() + ")");
		}
		return publicacio;
	}
	


	
	
	public boolean isAdminEntitat(Long procedimentId) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isAdminEntitat( procedimentId=" + procedimentId + ")");
		
		MetaExpedientEntity procediment = metaExpedientRepository.findOne(procedimentId);
		
		boolean isAdminEntitat = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = procediment.getEntitat();
		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("IPA_ADMIN"))) {
			isAdminEntitat = permisosHelper.isGrantedAll(entitat.getId(), EntitatEntity.class, new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
		} 
		return isAdminEntitat;
	}
	
	
	public boolean isAdminOrgan(Long organGestorId) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isAdminOrgan( organGestorId=" + organGestorId + ")");
		
		boolean isAdminOrgan = false;
		
		if (organGestorId != null) {
			OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organGestorId);
			List<OrganGestorEntity> organsGestors = organGestorHelper.findPares(organGestorEntity, true);
			permisosHelper.filterGrantedAny(
					organsGestors,
					OrganGestorEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION });
			isAdminOrgan = Utils.isNotEmpty(organsGestors);
		}
		return isAdminOrgan;
	}
	
	public boolean isGrantedPermisProcediment(
			Long procedimentId,
			Permission permission) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisProcediment( procedimentId=" + procedimentId + ")");
		
		boolean grantedMetaExpedient = permisosHelper.isGrantedAll(
				procedimentId,
				MetaNodeEntity.class,
				new Permission[] { permission });

		return grantedMetaExpedient;
	}
	
	public boolean isGrantedPermisOrgan(
			Long organGestorId,
			Permission permission) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisOrgan( organGestorId=" + organGestorId + ")");
		
		boolean grantedOrgan = false;

		if (organGestorId != null) {
			OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organGestorId);
			List<OrganGestorEntity> organsGestors = organGestorHelper.findPares(organGestorEntity, true);
			permisosHelper.filterGrantedAny(
					organsGestors,
					OrganGestorEntity.class,
					new Permission[] { permission});
			grantedOrgan = Utils.isNotEmpty(organsGestors);
		}

		return grantedOrgan;
	}
	
	public boolean isGrantedPermisProcedimentOrgan(
			Long procedimentId,
			Permission permission) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisProcedimentOrgan( procedimentId=" + procedimentId + ")");
		
		List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors = organGestorRepository.findMetaExpedientOrganGestorsByMetaExpedientId(procedimentId);

		permisosHelper.filterGrantedAll(
				metaExpedientOrgansGestors,
				MetaExpedientOrganGestorEntity.class,
				new Permission[] { permission });
		
		boolean grantedOrganProcediment = Utils.isNotEmpty(metaExpedientOrgansGestors);
		return grantedOrganProcediment;
	}
	
	public boolean isGrantedPermisProcedimentOrgan(
			Long procedimentId,
			Long organGestorId,
			Permission permission) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisProcedimentOrgan (procedimentId=" + procedimentId + ", organGestorId" + organGestorId + ")");
		
		List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors = new ArrayList<>();
		
		List<Long> organGestorIdAmbPares = organGestorHelper.findParesIds(
				organGestorId,
				true);
		for (Long org: organGestorIdAmbPares) {
			MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientIdAndOrganGestorId(
					procedimentId,
					org); 
			
			if (metaExpedientOrganGestor != null) {
				metaExpedientOrgansGestors.add(metaExpedientOrganGestor);
			}
			
		}

		permisosHelper.filterGrantedAll(
				metaExpedientOrgansGestors,
				MetaExpedientOrganGestorEntity.class,
				new Permission[] { permission });
		
		boolean grantedOrganProcediment = Utils.isNotEmpty(metaExpedientOrgansGestors);
		return grantedOrganProcediment;
	}
	
	public boolean isGrantedPermisProcediemntsComuns(
			Long procedimentId,
			Permission permission) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisProcediemntsComuns( procedimentId=" + procedimentId + ")");
		
		boolean grantedOrganProcedimentsComuns = false;
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(procedimentId);
		if (metaExpedient.isComu()) {
			List<Serializable> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(
					OrganGestorEntity.class,
					ExtendedPermission.COMU,
					permission);
			if (Utils.isNotEmpty(organProcedimentsComunsIds) ) {
				grantedOrganProcedimentsComuns = true;
			}
		}
		return grantedOrganProcedimentsComuns;
	}
	
	public boolean isGrantedPermisProcedimentsComuns(
			Long procedimentId,
			Long organId,
			Permission permission) {
		
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(procedimentId);
		OrganGestorEntity organ = organGestorRepository.findOne(organId);
		
		boolean grantedOrganProcedimentsComuns = false;
		
		if (metaExpedient.isComu()) {
			List<Long> organParesIds = organGestorHelper.findParesIds(organ.getId(), true);
			permisosHelper.filterGrantedAll(
					organParesIds,
					OrganGestorEntity.class,
					new Permission[] { ExtendedPermission.COMU, permission },
					SecurityContextHolder.getContext().getAuthentication()); 
			boolean isGrantedProcedimentsComuns = Utils.isNotEmpty(organParesIds);
			if (isGrantedProcedimentsComuns) {
				grantedOrganProcedimentsComuns = true;
			}
		}
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("isGrantedPermisProcedimentsComuns (procedimentId=" + procedimentId + ", organGestorId" + organId + ", grantedOrganProcedimentsComuns" + grantedOrganProcedimentsComuns + ")");
		return grantedOrganProcedimentsComuns;
	}
	
	
	public void comprovarPermisMetaExpedient(
			Long procedimentId,
			Permission permission,
			String permissionName,
			String rolActual) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("comprovarPermisMetaExpedient (procedimentId=" + procedimentId + ", permissionName=" + permissionName + ", rolActual=" + rolActual + ", user=" + SecurityContextHolder.getContext().getAuthentication().getName());
		
		OrganGestorEntity organ = metaExpedientRepository.findOne(procedimentId).getOrganGestor();
		Long organId = organ != null ? organ.getId() : null;
		
		if (!isAdminEntitat(procedimentId)) {
			
			if (!isAdminOrgan(organId)) {
				
				if (!isGrantedPermisProcediment(
						procedimentId,
						permission)) {

					if (!isGrantedPermisOrgan(
							organId,
							permission)) {
						
						if (!isGrantedPermisProcedimentOrgan(
								procedimentId,
								permission)) {
						
							if (!isGrantedPermisProcediemntsComuns(
									procedimentId,
									permission)) {
								
								throw new PermissionDeniedException(
										procedimentId,
										MetaExpedientEntity.class,
										permissionName);
							}	
						}
					}
				}
			}
		}
	}
	
	
	public void comprovarPermisExpedientCreation(
			Long procedimentId,
			Long organId, 
			Long grupId, 
			String rolActual) {
		
		if (cacheHelper.mostrarLogsPermisos())
			logger.info("comprovarPermisExpedientCreation (procedimentId=" + procedimentId + ", organId=" + organId + ", grupId=" + grupId + ", rolActual=" + rolActual + ", user=" + SecurityContextHolder.getContext().getAuthentication().getName());
		
		if (!isAdminEntitat(procedimentId)) {
			
			if (!isAdminOrgan(organId)) {

				if (!isGrantedPermisProcediment(
						procedimentId,
						ExtendedPermission.CREATE)) {

					if (!isGrantedPermisOrgan(
							organId,
							ExtendedPermission.CREATE)) {

						if (!isGrantedPermisProcedimentOrgan(
								procedimentId,
								organId,
								ExtendedPermission.CREATE)) {

							if (!isGrantedPermisProcedimentsComuns(
									procedimentId,
									organId,
									ExtendedPermission.CREATE)) {
								
								throw new PermissionDeniedException(
										procedimentId,
										MetaExpedientEntity.class,
										"CREATE");
							}
						}
					}

					if (grupId != null) {
						GrupEntity grup = grupRepository.findOne(grupId);
						if (grup != null) {
							
							boolean grantedGrup = permisosHelper.isGrantedAll(
									grup.getId(),
									GrupEntity.class,
									new Permission[] { ExtendedPermission.READ });
							if (!grantedGrup) {
								throw new PermissionDeniedException(
										grup.getId(),
										grup.getClass(),
										"GRUP");
							}
						}
					}
				}
			}
		}
	}
	
	

	public boolean comprovarPermisExpedient(
			Long expedientId,
			Permission permission,
			String permissionName, 
			boolean throwException) {

		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		Long procedimentId = expedient.getMetaExpedient().getId();
		Long organId = expedient.getOrganGestor() != null ? expedient.getOrganGestor().getId() : null;

		if (cacheHelper.mostrarLogsPermisos())
			logger.info("comprovarPermisExpedient (expedientId=" + expedientId + ", permission=" + permissionName + ", user=" + SecurityContextHolder.getContext().getAuthentication().getName());
		
		if (!isAdminEntitat(procedimentId)) {

			if (!isAdminOrgan(organId)) {

				if (!isGrantedPermisProcediment(
						procedimentId,
						permission)) {

					if (!isGrantedPermisOrgan(
							expedient.getOrganGestor().getId(),
							permission)) {

						if (!isGrantedPermisProcedimentOrgan(
								procedimentId,
								organId,
								permission)) {

							if (!isGrantedPermisProcedimentsComuns(
									procedimentId,
									organId,
									permission)) {
								if (throwException) {
									throw new PermissionDeniedException(
											expedient.getId(),
											expedient.getClass(),
											permissionName);
								} else {
									return false;
								}
							}
						}
					}
				}

				GrupEntity grup = expedientRepository.findOne(expedientId).getGrup();
				if (grup != null) {
					boolean grantedGrup = permisosHelper.isGrantedAll(
							grup.getId(),
							GrupEntity.class,
							new Permission[] { ExtendedPermission.READ });
					if (!grantedGrup) {
						if (throwException) {
							throw new PermissionDeniedException(
									grup.getId(),
									grup.getClass(),
									"GRUP");
						} else {
							return false;
						}						
					}
				}
			}
		}
		return true;
	}
	
	

	
	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntityComprovarHelper.class);

}