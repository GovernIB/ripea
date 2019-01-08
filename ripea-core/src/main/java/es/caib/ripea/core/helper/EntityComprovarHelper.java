/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPublicacioRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
import es.caib.ripea.core.repository.NodeRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import es.caib.ripea.core.security.ExtendedPermission;


/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaNodeRepository metaNodeRepository;
	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private MetaExpedientRepository metaExpedientRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private NodeRepository nodeRepository;
	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private CarpetaRepository carpetaRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DadaRepository dadaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private InteressatRepository interessatRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private DocumentPublicacioRepository documentPublicacioRepository;

	@Resource
	private PermisosHelper permisosHelper;



	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdmin,
			boolean comprovarPermisUsuariOrAdmin) throws NotFoundException {
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisUsuari) {
			boolean esLectorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!esLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"READ");
			}
		}
		if (comprovarPermisAdmin) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
					auth);
			if (!esAdministradorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		}
		if (comprovarPermisUsuariOrAdmin) {
			boolean esAdministradorOLectorEntitat = permisosHelper.isGrantedAny(
					entitatId,
					EntitatEntity.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ},
					auth);
			if (!esAdministradorOLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION || READ");
			}
		}
		return entitat;
	}

	public MetaNodeEntity comprovarMetaNode(
			EntitatEntity entitat,
			Long id) {
		MetaNodeEntity metaNode = metaNodeRepository.findOne(
				id);
		if (metaNode == null) {
			throw new NotFoundException(
					id,
					MetaNodeEntity.class);
		}
		if (!entitat.equals(metaNode.getEntitat())) {
			throw new ValidationException(
					id,
					MetaNodeEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del meta-node");
		}
		return metaNode;
	}

	public MetaExpedientEntity comprovarMetaExpedient(
			EntitatEntity entitat,
			Long id) {
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(
				id);
		if (metaExpedient == null) {
			throw new NotFoundException(
					id,
					MetaExpedientEntity.class);
		}
		if (!entitat.equals(metaExpedient.getEntitat())) {
			throw new ValidationException(
					id,
					MetaExpedientEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del meta-expedient");
		}
		return metaExpedient;
	}
	public MetaExpedientEntity comprovarMetaExpedient(
			EntitatEntity entitat,
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete) {
		MetaExpedientEntity metaExpedient = comprovarMetaExpedient(entitat, id);
		if (comprovarPermisCreate) {
			if (!metaExpedient.isActiu()) {
				throw new ValidationException(
						id,
						MetaExpedientEntity.class,
						"El meta-expedient no es troba actiu (id=" + id + ")");
			}
		}
		comprovarPermisosMetaNode(
				metaExpedient,
				id,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete);
		return metaExpedient;
	}

	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id) {
		MetaDocumentEntity metaDocument = metaDocumentRepository.findOne(
				id);
		if (metaDocument == null) {
			throw new NotFoundException(
					id,
					MetaDocumentEntity.class);
		}
		if (!entitat.equals(metaDocument.getEntitat())) {
			throw new ValidationException(
					id,
					MetaDocumentEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del meta-document");
		}
		if (!metaExpedient.equals(metaDocument.getMetaExpedient())) {
			throw new ValidationException(
					id,
					MetaDocumentEntity.class,
					"El meta-expedient especificat (id=" + metaExpedient.getId() + ") no coincideix amb el meta-expedient del meta-document");
		}
		return metaDocument;
	}
	public MetaDocumentEntity comprovarMetaDocument(
			EntitatEntity entitat,
			MetaExpedientEntity metaExpedient,
			Long id,
			boolean comprovarActiu) {
		MetaDocumentEntity metaDocument = comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		if (comprovarActiu) {
			if (!metaDocument.isActiu()) {
				throw new ValidationException(
						id,
						MetaDocumentEntity.class,
						"El meta-document no es troba actiu (id=" + id + ")");
			}
		}return metaDocument;
	}

	public MetaDadaEntity comprovarMetaDada(
			EntitatEntity entitat,
			MetaNodeEntity metaNode,
			Long id) {
		MetaDadaEntity metaDada = metaDadaRepository.findOne(id);
		if (metaDada == null) {
			throw new NotFoundException(
					id,
					MetaDadaEntity.class);
		}
		if (!metaNode.equals(metaDada.getMetaNode())) {
			throw new ValidationException(
					id,
					MetaDadaEntity.class,
					"El meta-node especificat (id=" + metaNode.getId() + ") no coincideix amb el meta-node de la meta-dada");
		}
		if (!entitat.equals(metaDada.getMetaNode().getEntitat())) {
			throw new ValidationException(
					id,
					MetaExpedientEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del meta-expedient");
		}
		return metaDada;
	}

	public ContingutEntity comprovarContingut(
			EntitatEntity entitat,
			Long id) {
		ContingutEntity contingut = contingutRepository.findOne(id);
		if (contingut == null) {
			throw new NotFoundException(
					id,
					ContingutEntity.class);
		}
		if (!contingut.getEntitat().equals(entitat)) {
			throw new ValidationException(
					id,
					ContingutEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del contingut");
		}
		return contingut;
	}

	public NodeEntity comprovarNode(
			EntitatEntity entitat,
			Long nodeId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete) {
		NodeEntity node = nodeRepository.findOne(nodeId);
		if (node == null) {
			throw new NotFoundException(
					nodeId,
					NodeEntity.class);
		}
		if (!entitat.equals(node.getEntitat())) {
			throw new ValidationException(
					nodeId,
					NodeEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del node");
		}
		comprovarPermisosMetaNode(
				node.getMetaNode(),
				nodeId,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete);
		return node;
	}

	public CarpetaEntity comprovarCarpeta(
			EntitatEntity entitat,
			Long id) {
		CarpetaEntity carpeta = carpetaRepository.findOne(id);
		if (carpeta == null) {
			throw new NotFoundException(
					id,
					CarpetaEntity.class);
		}
		if (!entitat.equals(carpeta.getEntitat())) {
			throw new ValidationException(
					id,
					CarpetaEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la carpeta");
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
			boolean comprovarPermisDelete) {
		EntitatEntity entitat = comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		if (expedient == null) {
			throw new NotFoundException(
					expedientId,
					ExpedientEntity.class);
		}
		if (expedient.getEsborrat() != 0) {
			throw new NotFoundException(
					expedientId,
					ExpedientEntity.class);
		}
		if (!entitat.getId().equals(expedient.getEntitat().getId())) {
			throw new ValidationException(
					expedientId,
					ExpedientEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'expedient");
		}
		if (comprovarAgafatPerUsuariActual) {
			UsuariEntity agafatPer = expedient.getAgafatPer();
			if (agafatPer != null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(agafatPer.getCodi())) {
					throw new ValidationException(
							expedientId,
							ContingutEntity.class,
							"L'expedient no està agafat per l'usuari actual (" +
							"usuariActualCodi=" + auth.getName() + ")");
				}
			} else {
				throw new ValidationException(
						expedientId,
						ContingutEntity.class,
						"L'expedient no està agafat per cap usuari");
			}
		}

		// if expedient estat has write permissions don't need to check metaExpedient permissions
		if (comprovarPermisWrite && expedient.getExpedientEstat()!=null) {
			if (hasEstatWritePermissons(expedient.getExpedientEstat().getId()))
				comprovarPermisWrite = false;
		}
		
		comprovarPermisosMetaNode(
				expedient.getMetaExpedient(),
				expedientId,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete);
		return expedient;
	}
	
	
	/**
	 * checking if expedient estat has modify permissions
	 * @param estatId
	 * @return
	 */
	public boolean hasEstatWritePermissons(Long estatId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		return permisosHelper.isGrantedAll(
				estatId,
				ExpedientEstatEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);
	}
	
	
	/**
	 * checking if expedient estat has modify permissions
	 * @param estatId
	 * @return
	 */
	public boolean hasMetaExpedientWritePermissons(Long metaExpedientId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		return permisosHelper.isGrantedAll(
				metaExpedientId,
				MetaExpedientEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);
	}

	public DocumentEntity comprovarDocument(
			EntitatEntity entitat,
			ExpedientEntity expedient,
			Long documentId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete) {
		DocumentEntity document = documentRepository.findOne(documentId);
		if (document == null) {
			throw new NotFoundException(
					documentId,
					DocumentEntity.class);
		}
		if (!document.getEntitat().equals(entitat)) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del document");
		}
		if (expedient != null && !document.getExpedient().equals(expedient)) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"L'expedient especificat (id=" + expedient.getId() + ") no coincideix amb l'expedient del document (id=" + document.getExpedient().getId() + ")");
		}
		if (document.getMetaDocument() != null && (comprovarPermisRead || comprovarPermisWrite || comprovarPermisDelete)) {
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
			boolean granted = permisosHelper.isGrantedAll(
					document.getMetaDocument().getId(),
					MetaNodeEntity.class,
					permisos.toArray(new Permission[permisos.size()]),
					auth);
			if (!granted) {
				throw new PermissionDeniedException(
						documentId,
						DocumentEntity.class,
						auth.getName(),
						permisosStr.toString());
			}
		}
		return document;
	}

	public DadaEntity comprovarDada(
			NodeEntity node,
			Long dadaId) {
		DadaEntity dada = dadaRepository.findOne(dadaId);
		if (dada == null) {
			throw new NotFoundException(
					dadaId,
					DadaEntity.class);
		}
		if (!dada.getNode().equals(node)) {
			throw new ValidationException(
					dadaId,
					DadaEntity.class,
					"El node especificat (id=" + node.getId() + ") no coincideix amb el node de la dada");
		}
		return dada;
	}

	/*public RegistreEntity comprovarRegistre(
			Long id,
			BustiaEntity bustiaPare) {
		RegistreEntity registre = registreRepository.findOne(id);
		if (registre == null) {
			throw new NotFoundException(
					id,
					RegistreEntity.class);
		}
		if (bustiaPare != null) {
			if (registre.getPare() != null) {
				if (!registre.getPare().getId().equals(bustiaPare.getId())) {
					throw new ValidationException(
							id,
							RegistreEntity.class,
							"La bústia especificada (id=" + bustiaPare.getId() + ") no coincideix amb la bústia de l'anotació de registre");
				}
			}
		}
		return registre;
	}*/

	public InteressatEntity comprovarInteressat(
			ExpedientEntity expedient,
			Long interessatId) {
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NotFoundException(
					interessatId,
					InteressatEntity.class);
		}
		if (expedient != null && !interessat.getExpedient().equals(expedient)) {
			throw new ValidationException(
					interessatId,
					InteressatEntity.class,
					"L'expedient especificat (id=" + expedient.getId() + ") no coincideix amb l'expedeint de l'interessat (id=" + interessat.getExpedient().getId() + ")");
		}
		return interessat;
	}

	public DocumentNotificacioEntity comprovarNotificacio(
			ExpedientEntity expedient,
			DocumentEntity document,
			Long notificacioId) {
		DocumentNotificacioEntity notificacio = documentNotificacioRepository.findOne(
				notificacioId);
		if (notificacio == null) {
			throw new NotFoundException(
					notificacioId,
					DocumentNotificacioEntity.class);
		}
		if (!notificacio.getExpedient().equals(expedient)) {
			throw new ValidationException(
					notificacioId,
					DocumentNotificacioEntity.class,
					"L'expedient especificat (id=" + expedient.getId() + ") no coincideix amb l'expedient de la notificació (id=" + notificacio.getExpedient().getId() + ")");
		}
		if (document != null && !notificacio.getDocument().equals(document)) {
			throw new ValidationException(
					notificacioId,
					DocumentNotificacioEntity.class,
					"El document especificat (id=" + document.getId() + ") no coincideix amb el document de la notificació (id=" + notificacio.getDocument().getId() + ")");
		}
		return notificacio;
	}

	public DocumentPublicacioEntity comprovarPublicacio(
			ExpedientEntity expedient,
			DocumentEntity document,
			Long publicacioId) {
		DocumentPublicacioEntity publicacio = documentPublicacioRepository.findOne(
				publicacioId);
		if (publicacio == null) {
			throw new NotFoundException(
					publicacioId,
					DocumentNotificacioEntity.class);
		}
		if (!publicacio.getExpedient().equals(expedient)) {
			throw new ValidationException(
					publicacioId,
					DocumentPublicacioEntity.class,
					"L'expedient especificat (id=" + expedient.getId() + ") no coincideix amb l'expedient de la publicació (id=" + publicacio.getExpedient().getId() + ")");
		}
		if (document != null && !publicacio.getDocument().equals(document)) {
			throw new ValidationException(
					publicacioId,
					DocumentPublicacioEntity.class,
					"El document especificat (id=" + document.getId() + ") no coincideix amb el document de la publicació (id=" + publicacio.getDocument().getId() + ")");
		}
		return publicacio;
	}

	public void comprovarPermisosMetaNode(
			MetaNodeEntity metaNode,
			Long nodeId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisRead) {
			boolean granted = permisosHelper.isGrantedAll(
					metaNode.getId(),
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per accedir al node ("
						+ "id=" + nodeId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		if (comprovarPermisWrite) {
			boolean granted = permisosHelper.isGrantedAll(
					metaNode.getId(),
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.WRITE},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per a modificar el node ("
						+ "id=" + nodeId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
//		if (comprovarPermisCreate) {
//			boolean granted = permisosHelper.isGrantedAll(
//					metaNode.getId(),
//					MetaNodeEntity.class,
//					new Permission[] {ExtendedPermission.WRITE},
//					auth);
//			if (!granted) {
//				throw new SecurityException("Sense permisos per a modificar el node ("
//						+ "id=" + nodeId + ", "
//						+ "usuari=" + auth.getName() + ")");
//			}
//		}
		if (comprovarPermisDelete) {
			boolean granted = permisosHelper.isGrantedAll(
					metaNode.getId(),
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.DELETE},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per a esborrar el node ("
						+ "id=" + nodeId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
	}

}