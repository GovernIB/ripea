/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaDadaHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.NodeRepository;

/**
 * Implementació del servei de gestió de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaDadaServiceImpl implements MetaDadaService {

	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private NodeRepository nodeRepository;
	@Resource
	private DadaRepository dadaRepository;

	@Resource
	ConversioTipusHelper conversioTipusHelper;
	@Resource
	PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetaNodeHelper metaNodeHelper;
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
	@Resource
	private MetaDadaHelper metaDadaHelper;

	@Transactional
	@Override
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {

		return metaDadaHelper.create(
				entitatId,
				metaNodeId,
				metaDada,
				rolActual,
				organId);
	}

	@Transactional
	@Override
	public MetaDadaDto update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {
		logger.debug("Actualitzant meta-dada existent (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"metaDada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity entity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDada.getId());
		
		
		Object valor = null;
		if (metaDada.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
			valor = metaDada.getValorBoolea();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.DATA) {
			valor = metaDada.getValorData();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
			valor = metaDada.getValorFlotant();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
			valor = metaDada.getValorImport();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.SENCER) {
			valor = metaDada.getValorSencer();
		}  else if (metaDada.getTipus()==MetaDadaTipusEnumDto.TEXT || metaDada.getTipus()==MetaDadaTipusEnumDto.DOMINI) {
			valor = metaDada.getValorString();
		}
		entity.update(
				metaDada.getCodi(),
				metaDada.getNom(),
				metaDada.getTipus(),
				metaDada.getMultiplicitat(),
				valor,
				metaDada.getDescripcio(),
				metaDada.isReadOnly(),
				metaDada.isNoAplica());
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		return conversioTipusHelper.convertir(
				entity,
				MetaDadaDto.class);
	}
	
	@Transactional
	@Override
	public MetaDadaDto delete(
			Long entitatId,
			Long metaNodeId,
			Long id, String rolActual, Long organId) {
		logger.debug("Esborrant meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		
		
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		metaDadaRepository.delete(metaDada);
		
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}

		return conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public MetaDadaDto updateActiva(
			Long entitatId,
			Long metaNodeId,
			Long id,
			boolean activa, String rolActual, Long organId) {
		logger.debug("Actualitzant propietat activa de la meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"id=" + id + "," +
				"activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		metaDada.updateActiva(activa);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		return conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public void moveUp(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap amunt ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaNodeId=" + metaNodeId +  ", "
				+ "metaDadaId=" + metaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaNode,
				metaDada,
				metaDada.getOrdre() - 1);
	}

	@Transactional
	@Override
	public void moveDown(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap avall ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaNodeId=" + metaNodeId +  ", "
				+ "metaDadaId=" + metaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaNode,
				metaDada,
				metaDada.getOrdre() + 1);
	}

	@Transactional
	@Override
	public void moveTo(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId,
			int posicio) {
		logger.debug("Movent meta-dada al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaNodeId=" + metaNodeId +  ", "
				+ "metaDadaId=" + metaDadaId +  ", "
				+ "posicio=" + posicio +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaNode,
				metaDada,
				posicio);
	}
	
	@Transactional(readOnly=true)
	@Override
	public MetaDadaDto findById(
			Long entitatId,
			Long metaNodeId,
			Long id) {
		logger.debug("Consulta de la meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		
		MetaDadaDto metaDadaDto = conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
		
		
		return metaDadaDto;
	}

	@Transactional(readOnly=true)
	@Override
	public MetaDadaDto findByCodi(
			Long entitatId,
			Long metaNodeId,
			String codi) {
		logger.debug("Consulta de la meta-dada per entitat i codi (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"codi=" + codi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		return conversioTipusHelper.convertir(
				metaDadaRepository.findByMetaNodeAndCodi(metaNode, codi),
				MetaDadaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public PaginaDto<MetaDadaDto> findByMetaNodePaginat(
			Long entitatId,
			Long metaNodeId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta paginada de les meta-dades de l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		PaginaDto<MetaDadaDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					metaDadaRepository.findByMetaNode(
							metaNode,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaDadaDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaDadaRepository.findByMetaNode(
							metaNode,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaDadaDto.class);
		}
		return resposta;
	}

	@Transactional(readOnly=true)
	@Override
	public List<MetaDadaDto> findActiveByMetaNode(
			Long entitatId,
			Long metaNodeId) {
		logger.debug("Consulta de les meta-dades de l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByMetaNodeAndActivaTrueOrderByOrdreAsc(metaNode),
				MetaDadaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<MetaDadaDto> findByNode(
			Long entitatId,
			Long nodeId) {
		logger.debug("Consulta de les meta-dades disponibles al node ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + nodeId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		NodeEntity node = entityComprovarHelper.comprovarNode(entitat, nodeId);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByMetaNodeAndActivaTrueOrderByOrdreAsc(node.getMetaNode()),
				MetaDadaDto.class);
	}

	@Override
	public Long findMetaNodeIdByNodeId(
			Long entitatId,
			Long nodeId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		NodeEntity node = entityComprovarHelper.comprovarNode(entitat, nodeId);
		return node.getMetaNode().getId();
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDadaServiceImpl.class);

}