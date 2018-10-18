/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaNodeAmbMetaDadesDto;
import es.caib.ripea.core.api.dto.MetaNodeDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Utilitat per omplir les meta-dades dels meta-nodes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MetaNodeHelper {

	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;

	public void omplirMetaDadesPerMetaNodes(
			List<? extends MetaNodeAmbMetaDadesDto> metaNodes) {
		List<Long> metaNodeIds = new ArrayList<Long>();
		for (MetaNodeAmbMetaDadesDto metaNode: metaNodes) {
			metaNodeIds.add(metaNode.getId());
		}
		List<MetaDadaEntity> metaDades = null;
		if (metaNodeIds.size() > 0) {
			metaDades = metaDadaRepository.findByMetaNodeIdInOrderByMetaNodeIdAscOrdreAsc(
					metaNodeIds);
		}
		for (MetaNodeAmbMetaDadesDto metaNode: metaNodes) {
			List<MetaDadaDto> dtos = new ArrayList<MetaDadaDto>();
			if (metaDades != null) {
				for (MetaDadaEntity metaDada: metaDades) {
					if (metaDada.getMetaNode().getId().equals(metaNode.getId())) {
						dtos.add(conversioTipusHelper.convertir(
								metaDada,
								MetaDadaDto.class));
					}
				}
			}
			metaNode.setMetaDades(dtos);
		}
	}

	public void omplirMetaDadesPerMetaNode(
			MetaNodeAmbMetaDadesDto metaNode) {
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeIdOrderByOrdreAsc(
				metaNode.getId());
		List<MetaDadaDto> dtos = new ArrayList<MetaDadaDto>();
		for (MetaDadaEntity metaDada: metaDades) {
			dtos.add(conversioTipusHelper.convertir(
					metaDada,
					MetaDadaDto.class));
		}
		metaNode.setMetaDades(dtos);
	}

	public void moureMetaNodeMetaDada(
			MetaNodeEntity metaNode,
			MetaDadaEntity metaDada,
			int posicio) {
		List<MetaDadaEntity> metaNodeMetaDades = metaDadaRepository.findByMetaNodeOrderByOrdreAsc(metaNode);
		int indexOrigen = -1;
		for (MetaDadaEntity md: metaNodeMetaDades) {
			if (md.getId().equals(metaDada.getId())) {
				indexOrigen = md.getOrdre();
				break;
			}
		}
		metaNodeMetaDades.add(
				posicio,
				metaNodeMetaDades.remove(indexOrigen));
		for (int i = 0; i < metaNodeMetaDades.size(); i++) {
			metaNodeMetaDades.get(i).updateOrdre(i);
		}
	}

	public void reordenarMetaDades(
			MetaNodeEntity metaNode) {
		List<MetaDadaEntity> metaNodeMetaDades = metaDadaRepository.findByMetaNodeOrderByOrdreAsc(metaNode);
		int ordre = 0;
		for (MetaDadaEntity metaDada: metaNodeMetaDades) {
			metaDada.updateOrdre(ordre++);
		}
	}

	public void omplirPermisosPerMetaNodes(
			List<? extends MetaNodeDto> metaNodes,
			boolean ambLlistaPermisos) {
		// Filtra les entitats per saber els permisos per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<MetaNodeDto> oie = new ObjectIdentifierExtractor<MetaNodeDto>() {
			public Long getObjectIdentifier(MetaNodeDto entitat) {
				return entitat.getId();
			}
		};
		List<MetaNodeDto> metaNodesCreate = new ArrayList<MetaNodeDto>();
		metaNodesCreate.addAll(metaNodes);
		permisosHelper.filterGrantedAll(
				metaNodesCreate,
				oie,
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.CREATE},
				auth);
		List<MetaNodeDto> metaNodesRead = new ArrayList<MetaNodeDto>();
		metaNodesRead.addAll(metaNodes);
		permisosHelper.filterGrantedAll(
				metaNodesRead,
				oie,
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		List<MetaNodeDto> metaNodesWrite = new ArrayList<MetaNodeDto>();
		metaNodesWrite.addAll(metaNodes);
		permisosHelper.filterGrantedAll(
				metaNodesWrite,
				oie,
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);
		List<MetaNodeDto> metaNodesDelete = new ArrayList<MetaNodeDto>();
		metaNodesDelete.addAll(metaNodes);
		permisosHelper.filterGrantedAll(
				metaNodesDelete,
				oie,
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.DELETE},
				auth);
		for (MetaNodeDto metaNode: metaNodes) {
			metaNode.setUsuariActualCreate(
					metaNodesCreate.contains(metaNode));
			metaNode.setUsuariActualRead(
					metaNodesRead.contains(metaNode));
			metaNode.setUsuariActualWrite(
					metaNodesWrite.contains(metaNode));
			metaNode.setUsuariActualDelete(
					metaNodesDelete.contains(metaNode));
		}
		// Obté els permisos per a totes les entitats només amb una consulta
		if (ambLlistaPermisos) {
			List<Long> ids = new ArrayList<Long>();
			for (MetaNodeDto metaNode: metaNodes)
				ids.add(metaNode.getId());
			Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
					ids,
					MetaNodeEntity.class);
			for (MetaNodeDto metaNode: metaNodes)
				metaNode.setPermisos(permisos.get(metaNode.getId()));
		}
	}

	public void omplirPermisosPerMetaNode(
			MetaNodeDto metaNode,
			boolean ambLlistaPermisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		metaNode.setUsuariActualCreate(
				permisosHelper.isGrantedAll(
						metaNode.getId(),
						MetaNodeEntity.class,
						new Permission[] {ExtendedPermission.CREATE},
						auth));
		metaNode.setUsuariActualRead(
				permisosHelper.isGrantedAll(
						metaNode.getId(),
						MetaNodeEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth));
		metaNode.setUsuariActualWrite(
				permisosHelper.isGrantedAll(
						metaNode.getId(),
						MetaNodeEntity.class,
						new Permission[] {ExtendedPermission.WRITE},
						auth));
		metaNode.setUsuariActualDelete(
				permisosHelper.isGrantedAll(
						metaNode.getId(),
						MetaNodeEntity.class,
						new Permission[] {ExtendedPermission.DELETE},
						auth));
		if (ambLlistaPermisos) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					metaNode.getId(),
					MetaNodeEntity.class);
			metaNode.setPermisos(permisos);
		}
	}

}
