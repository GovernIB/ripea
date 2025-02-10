package es.caib.ripea.service.helper;

import es.caib.ripea.core.persistence.entity.*;
import es.caib.ripea.core.persistence.repository.DocumentRepository;
import es.caib.ripea.core.persistence.repository.EntitatRepository;
import es.caib.ripea.core.persistence.repository.MetaDadaRepository;
import es.caib.ripea.core.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MetaDadaHelper {
	
	@Resource private MetaDocumentRepository metaDocumentRepository;
	@Resource private EntitatRepository entitatRepository;
	@Resource private MetaDadaRepository metaDadaRepository;
	@Resource private DocumentRepository documentRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private EntityComprovarHelper entityComprovarHelper;
	@Resource private MetaExpedientHelper metaExpedientHelper;

	public MetaDadaEntity findByMetaNodeAndCodi(MetaNodeEntity metaNode, String codi) {
		return metaDadaRepository.findByMetaNodeAndCodi(metaNode, codi);
	}
	
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {
		logger.debug("Creant una nova meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaNodeId=" + metaNodeId + ", " +
				"metaDada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(entitat, metaNodeId);
		
		int ordre = metaDadaRepository.countByMetaNode(metaNode);
		
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
		
		
		MetaDadaEntity entity = MetaDadaEntity.getBuilder(
				metaDada.getCodi(),
				metaDada.getNom(),
				metaDada.getTipus(),
				metaDada.getMultiplicitat(),		
				valor,
				metaDada.isReadOnly(),
				ordre,
				metaNode,
				metaDada.isNoAplica(),
				metaDada.isEnviable(),
				metaDada.getMetadadaArxiu()).
				descripcio(metaDada.getDescripcio()).
				build();
		
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
				metaDadaRepository.save(entity),
				MetaDadaDto.class);
	}
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDadaHelper.class);
	

}
