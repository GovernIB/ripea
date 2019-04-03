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
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
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

	@Transactional
	@Override
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) {
		logger.debug("Creant una nova meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"metaDada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(entitat, metaNodeId);
		
		int ordre = metaDadaRepository.countByMetaNode(metaNode);
		
		MetaDadaEntity entity = MetaDadaEntity.getBuilder(
				metaDada.getCodi(),
				metaDada.getNom(),
				metaDada.getTipus(),
				metaDada.getMultiplicitat(),
				metaDada.getValor(),
				metaDada.isReadOnly(),
				ordre,
				metaNode).
				descripcio(metaDada.getDescripcio()).
				build();
		return conversioTipusHelper.convertir(
				metaDadaRepository.save(entity),
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public MetaDadaDto update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) {
		logger.debug("Actualitzant meta-dada existent (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"metaDada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity entity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDada.getId());
		entity.update(
				metaDada.getCodi(),
				metaDada.getNom(),
				metaDada.getTipus(),
				metaDada.getMultiplicitat(),
				metaDada.getValor(),
				metaDada.getDescripcio(),
				metaDada.isReadOnly());
		return conversioTipusHelper.convertir(
				entity,
				MetaDadaDto.class);
	}
	
	
	

	


	@Transactional
	@Override
	public MetaDadaDto delete(
			Long entitatId,
			Long metaNodeId,
			Long id) {
		logger.debug("Esborrant meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		metaDadaRepository.delete(metaDada);
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
			boolean activa) {
		logger.debug("Actualitzant propietat activa de la meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"id=" + id + "," +
				"activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		metaDada.updateActiva(activa);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
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
	
	
//	
//	@Override
//	@Transactional
//	public void moveTo(
//			Long entitatId,
//			Long metaExpedientId,
//			Long metaDadaId,
//			int posicio) throws NotFoundException {
//		logger.debug("Movent metadada del expedient a la posició especificada ("
//				+ "entitatId=" + entitatId + ", "
//				+ "metaDadaId=" + metaDadaId + ", "
//				+ "posicio=" + posicio + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false);
//		
//		MetaDadaEntity metaDada = metaDadaRepository.findOne(metaDadaId);
//
//		canviPosicio(
//				metaDada,
//				posicio);
//
//	}
//	
//
//	
//	private void canviPosicio(
//			MetaDadaEntity metaDada,
//			int posicio) {
//		List<MetaDadaEntity> metadades = metaDadaRepository.findByMetaNodeOrderByOrdreAsc(
//				metaDada.getMetaNode());
//		if (posicio >= 0 && posicio < metadades.size()) {
//			if (posicio < metaDada.getOrdre()) {
//				for (MetaDadaEntity est: metadades) {
//					if (est.getOrdre() >= posicio && est.getOrdre() < metaDada.getOrdre()) {
//						est.updateOrdre(est.getOrdre() + 1);
//					}
//				}
//			} else if (posicio > metaDada.getOrdre()) {
//				for (MetaDadaEntity est: metadades) {
//					if (est.getOrdre() > metaDada.getOrdre() && est.getOrdre() <= posicio) {
//						est.updateOrdre(est.getOrdre() - 1);
//					}
//				}
//			}
//			metaDada.updateOrdre(posicio);
//		}
//	}	

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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		return conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
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
				false);
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
				true,
				false,
				false);
		NodeEntity node = entityComprovarHelper.comprovarNode(
				entitat,
				nodeId,
				false,
				false,
				false,
				false);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByMetaNodeAndActivaTrueOrderByOrdreAsc(node.getMetaNode()),
				MetaDadaDto.class);
	}

	/*@Transactional(readOnly=true)
	@Override
	public List<MetaDadaDto> findByNodePerCreacio(
			Long entitatId,
			Long nodeId) {
		logger.debug("Consulta de les meta-dades candidates a afegir pel node ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + nodeId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		NodeEntity node = entityComprovarHelper.comprovarNode(
				entitat,
				nodeId,
				false,
				false,
				false,
				false);
		List<MetaDadaEntity> metaDades = new ArrayList<MetaDadaEntity>();
		// De les meta-dades actives pel meta-node només deixa les que encara
		// es poden afegir al node especificat segons la multiplicitat.
		List<MetaNodeMetaDadaEntity> metaNodeMetaDades = metaNodeMetaDadaRepository.findByMetaNodeAndActivaTrue(node.getMetaNode());
		List<DadaEntity> dades = dadaRepository.findByNode(node);
		for (MetaNodeMetaDadaEntity metaNodeMetaDada: metaNodeMetaDades) {
			boolean afegir = true;
			for (DadaEntity dada: dades) {
				if (dada.getMetaDada().equals(metaNodeMetaDada.getMetaDada())) {
					if (metaNodeMetaDada.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1) || metaNodeMetaDada.getMultiplicitat().equals(MultiplicitatEnumDto.M_1))
						afegir = false;
					break;
				}
			}
			if (afegir)
				metaDades.add(metaNodeMetaDada.getMetaDada());
		}
		// Afegeix les meta-dades globals actives
		List<MetaDadaEntity> metaDadesGlobals = null;
		if (node instanceof ExpedientEntity) {
			metaDadesGlobals = metaDadaRepository.findByEntitatAndGlobalExpedientTrueAndActivaTrueOrderByIdAsc(
							entitat);
		}
		if (node instanceof DocumentEntity) {
			metaDadesGlobals = metaDadaRepository.findByEntitatAndGlobalDocumentTrueAndActivaTrueOrderByIdAsc(
							entitat);
		}
		if (metaDadesGlobals != null) {
			for (MetaDadaEntity metaDada: metaDadesGlobals) {
				boolean afegir = true;
				for (DadaEntity dada: dades) {
					if (dada.getMetaDada().equals(metaDada)) {
						if (metaDada.getGlobalMultiplicitat().equals(MultiplicitatEnumDto.M_0_1) || metaDada.getGlobalMultiplicitat().equals(MultiplicitatEnumDto.M_1))
							afegir = false;
						break;
					}
				}
				if (afegir)
					metaDades.add(metaDada);
			}
		}
		return conversioTipusHelper.convertirList(
				metaDades,
				MetaDadaDto.class);
	}*/

	private static final Logger logger = LoggerFactory.getLogger(MetaDadaServiceImpl.class);

}