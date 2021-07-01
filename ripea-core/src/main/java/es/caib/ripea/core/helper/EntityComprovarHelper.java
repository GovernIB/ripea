/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPublicacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
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
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
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
	private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired
	private PermisosHelper permisosHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	
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
			boolean esAdministradorOrganGestor = permisosHelper.isGrantedAny(
					id,
					OrganGestorEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					auth);
			if (!esAdministradorOrganGestor) {
				throw new PermissionDeniedException(id, OrganGestorEntity.class, auth.getName(),
				        "ADMINISTRATION");
			}	
		}
		return organGestor;
	}
	

	public OrganGestorEntity comprovarOrganGestorPerRolUsuari(
			EntitatEntity entitat,
			Long id) {
		OrganGestorEntity organGestor = organGestorRepository.findOne(id);
		if (organGestor == null) {
			throw new NotFoundException(id, OrganGestorEntity.class);
		}
		if (!entitat.equals(organGestor.getEntitat())) {
			throw new ValidationException(
					id,
					MetaNodeEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'òrgan gestor");
		}
		
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaNodeEntity.class,
				ExtendedPermission.READ));
		List<Long> metaExpedientIdPermesosPerEntitat = null;
		if (metaExpedientIdPermesos != null && !metaExpedientIdPermesos.isEmpty()) {
			metaExpedientIdPermesosPerEntitat = metaExpedientRepository.findIdsByEntitat(entitat, metaExpedientIdPermesos);
		}
		
		if (metaExpedientIdPermesosPerEntitat != null && !metaExpedientIdPermesosPerEntitat.isEmpty()) {
			//if user has assigned direct permissions for any metaexpedient of entitat, then he has permissions for all organs of this entitat
		} else {
			boolean existsInPermitted = false;
			List<OrganGestorEntity> organGestors = getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(entitat);
			
			for (OrganGestorEntity organGestorEntity : organGestors) {
				if (organGestorEntity.getId().equals(id)) {
					existsInPermitted = true;
				}
			}
			
			if (!existsInPermitted) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				throw new SecurityException(
						"Sense permisos de consulta sobre l'òrgan gestor (" +
						"id=" + id + ", " +
				        "usuari=" + auth.getName() + ")");
			}
			
		}

		return organGestor;
	}
	
	
	
	
	
	public List<OrganGestorEntity> getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(EntitatEntity entitat) {
		
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				ExtendedPermission.READ));
		organGestorHelper.afegirOrganGestorFillsIds(entitat, organIdPermesos);
		
		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				ExtendedPermission.READ));
		if (metaExpedientOrganIdPermesos != null && !metaExpedientOrganIdPermesos.isEmpty()) {
			List<Long> organsIdsPerMetaExpedientOrganIdPermesos = metaExpedientOrganGestorRepository.findOrganGestorIdsByMetaExpedientOrganGestorIds(metaExpedientOrganIdPermesos);
			organGestorHelper.afegirOrganGestorFillsIds(entitat, organsIdsPerMetaExpedientOrganIdPermesos);
			organIdPermesos.addAll(organsIdsPerMetaExpedientOrganIdPermesos);
		}
		
		List<Long> organsWithoutDuplicates = !organIdPermesos.isEmpty() ? new ArrayList<Long>(new HashSet<Long>(organIdPermesos)) : null;
	    List<OrganGestorEntity> organGestors = organGestorRepository.findByEntitatAndIds(entitat, organsWithoutDuplicates);
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

	public MetaExpedientEntity comprovarMetaExpedientPerExpedient(
			EntitatEntity entitat,
			Long metaExpedientId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin) {
		MetaExpedientEntity metaExpedient = comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		if (comprovarPermisCreate) {
			if (!metaExpedient.isActiu()) {
				throw new ValidationException(metaExpedientId, MetaExpedientEntity.class, "El meta-expedient no es troba actiu (id=" + metaExpedientId + ")");
			}
		}
		comprovarPermisosMetaNode(
				metaExpedient,
				null,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete, 
				checkPerMassiuAdmin);
		return metaExpedient;
	}

	public MetaExpedientEntity comprovarMetaExpedientAdmin(
			EntitatEntity entitat,
			Long id) {
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
			throw new ValidationException(id, MetaExpedientEntity.class, "El meta-expedient no té cap organ gestor asociat (id=" + id + ")");
		}
		// si no es administrador d'entitat comprovar si es administrador del seu organ gestor
		comprovarOrganGestorAdmin(
				entitat.getId(),
				metaExpedient.getOrganGestor().getId());
		return metaExpedient;
	}

	public MetaDocumentEntity comprovarMetaDocument(EntitatEntity entitat, Long metaDocumentId) {
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
		if (!entitat.equals(metaDocumentEntitat)) {
			throw new ValidationException(metaDocumentId, MetaDocumentEntity.class,
			        "L'entitat especificada (id=" + entitat.getId()
			                + ") no coincideix amb l'entitat del meta-document");
		}
		if (metaDocument.getMetaExpedient() != null) {
			comprovarMetaExpedient(entitat, metaDocument.getMetaExpedient().getId());
		}

		return metaDocument;
	}

	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id) {
		MetaDocumentEntity metaDocument = metaDocumentRepository.findOne(id);
		if (metaDocument == null) {
			throw new NotFoundException(id, MetaDocumentEntity.class);
		}
		EntitatEntity metaDocumentEntitat = metaDocument.getEntitat();
		if (HibernateHelper.isProxy(metaDocumentEntitat)) {
			metaDocumentEntitat = HibernateHelper.deproxy(metaDocumentEntitat);
		}
		if (!entitat.equals(metaDocumentEntitat)) {
			throw new ValidationException(id, MetaDocumentEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del meta-document");
		}
		if (metaExpedient != null && !metaExpedient.equals(metaDocument.getMetaExpedient())) {
			throw new ValidationException(id, MetaDocumentEntity.class, "El meta-expedient especificat (id="
			        + metaExpedient.getId() + ") no coincideix amb el meta-expedient del meta-document");
		}
		return metaDocument;
	}

	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id,
			boolean comprovarActiu,
			boolean comprovarMetaExpedient) {
		MetaDocumentEntity metaDocument;
		if (comprovarMetaExpedient) {
			metaDocument = comprovarMetaDocument(
					entitat,
					metaExpedient,
					id);
		} else {
			metaDocument = comprovarMetaDocument(
					entitat,
					id);
		}
		if (comprovarActiu) {
			if (!metaDocument.isActiu()) {
				throw new ValidationException(id, MetaDocumentEntity.class,
				        "El meta-document no es troba actiu (id=" + id + ")");
			}
		}
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

	public ContingutEntity comprovarContingut(EntitatEntity entitat, Long id) {
		ContingutEntity contingut = contingutRepository.findOne(id);
		if (contingut == null) {
			throw new NotFoundException(id, ContingutEntity.class);
		}
		if (!contingut.getEntitat().getId().equals(entitat.getId())) {
			throw new ValidationException(id, ContingutEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat del contingut");
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
		if (!entitat.getId().equals(node.getEntitat().getId())) {
			throw new ValidationException(
					nodeId,
					NodeEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del node");
		}
		return node;
	}

	public CarpetaEntity comprovarCarpeta(EntitatEntity entitat, Long id) {
		CarpetaEntity carpeta = carpetaRepository.findOne(id);
		if (carpeta == null) {
			throw new NotFoundException(id, CarpetaEntity.class);
		}
		if (!entitat.equals(carpeta.getEntitat())) {
			throw new ValidationException(id, CarpetaEntity.class, "L'entitat especificada (id="
			        + entitat.getId() + ") no coincideix amb l'entitat de la carpeta");
		}
		return carpeta;
	}

	public ExpedientEntity comprovarExpedient(
			Long entitatId,
			Long expedientId,
			boolean comprovarAgafatPerUsuariActual,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin) {
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
		if (comprovarAgafatPerUsuariActual) {
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
		// if expedient estat has write permissions don't need to check metaExpedient
		// permissions
		if (comprovarPermisWrite && expedient.getExpedientEstat() != null) {
			if (hasEstatWritePermissons(expedient.getExpedientEstat().getId()))
				comprovarPermisWrite = false;
		}
		comprovarPermisosMetaNode(
				expedient.getMetaExpedient(),
				expedientId,
				comprovarPermisRead,
		        comprovarPermisWrite,
		        comprovarPermisCreate,
		        comprovarPermisDelete,
		        checkPerMassiuAdmin);
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
	 * @param estatId
	 * @return
	 */
	public boolean hasEstatWritePermissons(Long estatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return permisosHelper.isGrantedAll(estatId, ExpedientEstatEntity.class,
		        new Permission[] { ExtendedPermission.WRITE }, auth);
	}

	/**
	 * checking if expedient estat has modify permissions
	 * 
	 * @param estatId
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
	
	public void comprovarPermisosMetaNode(
			Long metaNodeId,
			Long nodeId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin) {

		MetaNodeEntity metaNodeEntity = metaNodeRepository.findOne(metaNodeId);
		
		comprovarPermisosMetaNode(
				metaNodeEntity,
				nodeId,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete,
				checkPerMassiuAdmin);
	}
	
	


	protected void comprovarPermisosMetaNode(
			MetaNodeEntity metaNode,
			Long nodeId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin) {
		boolean metaExpedientBelongsToEntitatOrOrgansOfUser = false;
		if (metaNode.getClass() == MetaExpedientEntity.class) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
					metaNode.getEntitat().getId(),
					EntitatEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					auth);
			List<MetaExpedientEntity> metaExpedients = null;
			List<OrganGestorEntity> organs = organGestorHelper.findAmbEntitatPermis(
					metaNode.getEntitat(),
					ExtendedPermission.ADMINISTRATION);
			boolean metaExpedientBelongsToOrgans = false;
			if (organs != null && !organs.isEmpty()) {
				metaExpedients = metaExpedientRepository.findByOrganGestors(
						metaNode.getEntitat(),
						organs);
			}
			if (metaExpedients != null) {
				for (MetaExpedientEntity metaExpedientEntity: metaExpedients) {
					if (metaExpedientEntity.getId().equals(metaNode.getId())) {
						metaExpedientBelongsToOrgans = true;
					}
				}
			}
			if (esAdministradorEntitat || metaExpedientBelongsToOrgans) {
				metaExpedientBelongsToEntitatOrOrgansOfUser = true;
			}
			
			if (metaExpedientBelongsToEntitatOrOrgansOfUser) {
				comprovarPermisRead = false;
			}
			
			if (checkPerMassiuAdmin && metaExpedientBelongsToEntitatOrOrgansOfUser) {
				comprovarPermisWrite = false;
			}
		}

		if (comprovarPermisRead) {
			comprovarPermisMetaNode(
					metaNode,
					nodeId,
					ExtendedPermission.READ,
					"READ",
					null);
		}
		if (comprovarPermisWrite) {
			comprovarPermisMetaNode(
					metaNode,
					nodeId,
					ExtendedPermission.WRITE,
					"WRITE",
					null);
		}
		if (comprovarPermisCreate) {
		comprovarPermisMetaNode(
				metaNode,
				nodeId,
				ExtendedPermission.CREATE,
				"CREATE",
				null);
		}
		if (comprovarPermisDelete) {
			comprovarPermisMetaNode(
					metaNode,
					nodeId,
					ExtendedPermission.DELETE,
					"DELETE",
					null);
		}
	}

	public void comprovarPermisMetaNode(
			MetaNodeEntity metaNode,
			Long nodeId,
			Permission permission,
			String permissionName,
			String usuariCodi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Per a tenir permís s'ha de donar, com a mínim, un d'aquests casos:
		// - Permis assignat directament al meta-node
		// - Permís assignat a l'òrgan del node (o a un dels òrgans pare)
		// - Permís assignat a la combinació òrgan + meta-node (o a una combinació òrgan pare + meta-node) en el cas en que sigui un expedient comú.
		
		boolean grantedDirect;
		if (usuariCodi != null) {
			grantedDirect = permisosHelper.isGrantedAll(
					metaNode.getId(),
					MetaNodeEntity.class,
					new Permission[] { permission },
					usuariCodi);
		} else {
			grantedDirect = permisosHelper.isGrantedAll(
					metaNode.getId(),
					MetaNodeEntity.class,
					new Permission[] { permission },
					auth);
		}


		if (metaNode.getClass() == MetaExpedientEntity.class ) {
			boolean grantedOrgan = false;
			boolean grantedOrganMetaNode = false;
			
			if (!grantedDirect) {
				
				List<OrganGestorEntity> organsGestors = new ArrayList<>();
				if (nodeId != null) {
					organsGestors = expedientOrganPareRepository.findOrganGestorByExpedientId(nodeId);
				} else {
					OrganGestorEntity organGestorEntity = ((MetaExpedientEntity) metaNode).getOrganGestor();
					if (organGestorEntity != null) {
						organsGestors = organGestorRepository.findOrganGestorsPath(organGestorEntity.getId());
					}
				}
				if (usuariCodi != null) {
					permisosHelper.filterGrantedAll(
							organsGestors,
							OrganGestorEntity.class,
							new Permission[] { permission },
							usuariCodi);
				} else {
					permisosHelper.filterGrantedAll(
							organsGestors,
							OrganGestorEntity.class,
							new Permission[] { permission });
				}
				grantedOrgan = !organsGestors.isEmpty();
			}
			
			if (!grantedDirect && !grantedOrgan) {
				List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors;
				if (nodeId != null) {
					metaExpedientOrgansGestors = expedientOrganPareRepository.findMetaExpedientOrganGestorByExpedientId(nodeId);
				} else {
					metaExpedientOrgansGestors = organGestorRepository.findMetaExpedientOrganGestorsByMetaExpedientId(metaNode.getId());
				}
				if (usuariCodi != null) {
					permisosHelper.filterGrantedAll(
							metaExpedientOrgansGestors,
							MetaExpedientOrganGestorEntity.class,
							new Permission[] { permission },
							usuariCodi);
				} else {
					permisosHelper.filterGrantedAll(
							metaExpedientOrgansGestors,
							MetaExpedientOrganGestorEntity.class,
							new Permission[] { permission });
				}
				grantedOrganMetaNode = !metaExpedientOrgansGestors.isEmpty();
			}
			

			if (!grantedDirect && !grantedOrgan && !grantedOrganMetaNode) {
				throw new PermissionDeniedException(metaNode.getId(), metaNode.getClass(), auth.getName(), permissionName);
			}
				
		} else {
			if (!grantedDirect) {
				throw new PermissionDeniedException(metaNode.getId(), metaNode.getClass(), auth.getName(), permissionName);
			}
		}

	}
	
	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

}