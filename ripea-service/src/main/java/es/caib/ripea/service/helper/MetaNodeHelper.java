package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaNodeAmbMetaDadesDto;
import es.caib.ripea.service.intf.dto.MetaNodeDto;
import es.caib.ripea.service.permission.ExtendedPermission;

@Component
public class MetaNodeHelper {

	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;

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
		moveTo(
				metaDada,
				metaNodeMetaDades,
				posicio);
	}
	
	public void moveTo(
			MetaDadaEntity elementToMove,
			List<MetaDadaEntity> elements,
			int posicio) {
		
		int anteriorIndex = -1; 
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getId().equals(elementToMove.getId())) {
				anteriorIndex = i;
				break;
			}
		}
		elements.add(
				posicio,
				elements.remove(anteriorIndex));
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).updateOrdre(i);
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

			for (MetaNodeDto metaNode: metaNodes) {
				metaNode.setPermisos(metaExpedientHelper.permisFind(metaNode.getId()));
			}
			
		}
	}
	
	


}
