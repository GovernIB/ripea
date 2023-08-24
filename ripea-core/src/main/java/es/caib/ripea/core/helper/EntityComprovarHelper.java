/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
		
		organIdPermesos = Utils.getUniqueValues(organIdPermesos);
		
		List<OrganGestorEntity> organGestors = new ArrayList<>();
		// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
		List<List<Long>> sublists = org.apache.commons.collections4.ListUtils.partition(organIdPermesos, 1000);

		for (List<Long> sublist : sublists) {
			organGestors.addAll(organGestorRepository.findByEntitatAndIds(entitat, sublist));
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
	
	
	


	
	public MetaExpedientEntity comprovarMetaExpedient(Long entitatId, Long metaExpedientId) {
		EntitatEntity entitat = comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				false,
				false);
		return comprovarMetaExpedient(entitat, metaExpedientId);
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
		comprovarPermisos(
				metaExpedient.getId(),
				null,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete, 
				rolActual, 
				organId);
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
		EntitatEntity entitat = comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				false,
				false);

		return comprovarMetaDocument(
				entitat,
				metaDocumentId);
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
		// if expedient estat has write permissions don't need to check metaExpedient
		// permissions
		if (comprovarPermisWrite && expedient.getEstatAdditional() != null) {
			if (hasEstatWritePermissons(expedient.getEstatAdditional().getId()))
				comprovarPermisWrite = false;
		}
		comprovarPermisos(
				null,
				expedientId,
				comprovarPermisRead,
		        comprovarPermisWrite,
		        comprovarPermisCreate,
		        comprovarPermisDelete,
		        rolActual, 
		        null);
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
	


	protected void comprovarPermisos(
			Long metaExpedientId,
			Long expedientId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			String rolActual, 
			Long organId) {
		
//			comprovar si l'expedient està relacionat i és una consulta de LECTURA sobre algún element de l'expedient, llavors no mirar permisos
		if (expedientId != null) {
			ContingutEntity contingut = contingutRepository.findOne(expedientId);
			boolean comprovarNomesLectura = (!comprovarPermisWrite && !comprovarPermisCreate && !comprovarPermisDelete);
			if (contingut instanceof ExpedientEntity && comprovarNomesLectura) {
				ExpedientEntity expedient = (ExpedientEntity)contingut;
				boolean relacionatAmbAlgunExpedient = expedient.getRelacionatsAmb() != null && !expedient.getRelacionatsAmb().isEmpty();
				boolean relacionatPerAlgunExpedient = expedient.getRelacionatsPer() != null && !expedient.getRelacionatsPer().isEmpty();
				if (relacionatAmbAlgunExpedient || relacionatPerAlgunExpedient) {
					comprovarPermisRead = false;
					comprovarPermisWrite = false;
				}
			}
		}

		if (comprovarPermisRead) {
			comprovarPermis(
					metaExpedientId,
					expedientId,
					ExtendedPermission.READ,
					"READ",
					null, 
					rolActual, 
					organId);
		}
		if (comprovarPermisWrite) {
			comprovarPermis(
					metaExpedientId,
					expedientId,
					ExtendedPermission.WRITE,
					"WRITE",
					null, 
					rolActual, 
					organId);
		}
		if (comprovarPermisCreate) {
		comprovarPermis(
				metaExpedientId,
				expedientId,
				ExtendedPermission.CREATE,
				"CREATE",
				null, 
				rolActual, 
				organId);
		}
		if (comprovarPermisDelete) {
			comprovarPermis(
					metaExpedientId,
					expedientId,
					ExtendedPermission.DELETE,
					"DELETE",
					null, 
					rolActual, 
					organId);
		}
		
	}

	public void comprovarPermis(
			Long metaExpedientId,
			Long expedientId,
			Permission permission,
			String permissionName,
			String usuariCodi, 
			String rolActual, 
			Long organChosenOnExpedientCreation) {//in order to check if is permited to create expedient (expedientId is still null then)
		
		
		MetaExpedientEntity metaExpedient = null;
		if (expedientId != null) {
			metaExpedient = expedientRepository.findOne(expedientId).getMetaExpedient();
			metaExpedientId = metaExpedient.getId();
		} else {
			metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		}
		
		boolean isAdminEntitat = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = metaExpedient.getEntitat();
		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("IPA_ADMIN"))) {
			isAdminEntitat = permisosHelper.isGrantedAll(entitat.getId(), EntitatEntity.class, new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
		} 
		
		if (!isAdminEntitat) {
		
			// Per a tenir permís s'ha de donar, com a mínim, un d'aquests casos:
			// - Permis assignat directament al meta-node
			// - Permís assignat a l'òrgan del node (o a un dels òrgans pare)
			// - Permís assignat a la combinació òrgan + meta-node (o a una combinació òrgan pare + meta-node) en el cas en que sigui un expedient comú.
			
			boolean grantedDirect;
			if (usuariCodi != null) {
				grantedDirect = permisosHelper.isGrantedAll(
						metaExpedientId,
						MetaNodeEntity.class,
						new Permission[] { permission },
						usuariCodi);
			} else {
				grantedDirect = permisosHelper.isGrantedAll(
						metaExpedientId,
						MetaNodeEntity.class,
						new Permission[] { permission },
						auth);
			}
	
			boolean grantedOrgan = false;
			boolean grantedOrganMetaNode = false;
			boolean grantedOrganProcedimentsComuns = false;
			
			if (!grantedDirect) {
				List<OrganGestorEntity> organsGestors = new ArrayList<>();
				if (expedientId != null) {
					organsGestors = expedientOrganPareRepository.findOrganGestorByExpedientId(expedientId);
				} else if (organChosenOnExpedientCreation != null) {
					OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organChosenOnExpedientCreation);
					organsGestors = organGestorHelper.findPares(organGestorEntity, true);
				} else {
					OrganGestorEntity organGestorEntity = metaExpedient.getOrganGestor();
					if (organGestorEntity != null) {
						organsGestors = organGestorHelper.findPares(organGestorEntity, true);
					}
				}
				if (usuariCodi != null) {
					permisosHelper.filterGrantedAny(
							organsGestors,
							OrganGestorEntity.class,
							new Permission[] { permission , ExtendedPermission.ADMINISTRATION },
							usuariCodi);
				} else {
					permisosHelper.filterGrantedAny(
							organsGestors,
							OrganGestorEntity.class,
							new Permission[] { permission , ExtendedPermission.ADMINISTRATION });
				}
				grantedOrgan = !organsGestors.isEmpty();
			}
			
			if (!grantedDirect && !grantedOrgan) {
				List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors;
				if (expedientId != null) {
					metaExpedientOrgansGestors = expedientOrganPareRepository.findMetaExpedientOrganGestorByExpedientId(expedientId);
				} else {
					metaExpedientOrgansGestors = organGestorRepository.findMetaExpedientOrganGestorsByMetaExpedientId(metaExpedientId);
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
				Long orgId = null;
				if (expedientId != null) {
					orgId = expedientRepository.findOne(expedientId).getOrganGestor().getId();
				} else {
					orgId = organChosenOnExpedientCreation;
				}
				if (orgId != null) {
					List<Long> organPathIds = organGestorHelper.findParesIds(orgId, true);
					permisosHelper.filterGrantedAll(
							organPathIds,
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.COMU, permission },
							auth);
					boolean isGrantedProcedimentsComuns = !organPathIds.isEmpty();
					if (isGrantedProcedimentsComuns && metaExpedient.isComu()) {
						grantedOrganProcedimentsComuns = true;
					}
				} else {
					// Cercam els òrgans amb permisos per procediemnts comuns
					List<Serializable> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(
							OrganGestorEntity.class,
							ExtendedPermission.COMU,
							permission);
					boolean accessAllComu = false;
					if (organProcedimentsComunsIds != null && !organProcedimentsComunsIds.isEmpty()) {
						accessAllComu = true;
					}
					if (accessAllComu && metaExpedient.isComu()) {
						grantedOrganProcedimentsComuns = true;
					}
				}
			}

			if (!grantedDirect && !grantedOrgan && !grantedOrganMetaNode && !grantedOrganProcedimentsComuns) {
				throw new PermissionDeniedException(metaExpedient.getId(), metaExpedient.getClass(), usuariCodi != null ? usuariCodi: auth.getName(), permissionName);
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