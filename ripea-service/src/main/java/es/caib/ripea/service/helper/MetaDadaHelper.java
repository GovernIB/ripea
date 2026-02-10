package es.caib.ripea.service.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import io.micrometer.core.instrument.Timer;

@Component
public class MetaDadaHelper {
	
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private CacheHelper cacheHelper;

	public MetaDadaEntity findByMetaNodeAndCodi(MetaNodeEntity metaNode, String codi) {
		return metaDadaRepository.findByMetaNodeAndCodi(metaNode, codi);
	}
	
	public MetaDadaEntity create(Long entitatId, Long metaNodeId, MetaDadaDto metaDada, String rolActual, Long organId) {
		
		Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());
		
		try {
		
			logger.debug("Creant una nova meta-dada (entitatId=" + entitatId + ", metaNodeId=" + metaNodeId + ", metaDada=" + metaDada + ")");
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

			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Procediment.metaDada", "resultado", "exito");
			
			evictValidacionsExpedients(entitat, metaNode);
			
			return metaDadaRepository.save(entity);
			
		} catch (Exception e) {
			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Procediment.metaDada", "resultado", "error");
			throw e;
		}			
	}
	
	public void evictValidacionsExpedients(Long entitatId, Long metaNodeId) {
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId);
		evictValidacionsExpedients(
				entitatEntity,
				entityComprovarHelper.comprovarMetaNode(entitatEntity, metaNodeId));
	}
	
	public void evictValidacionsExpedients(EntitatEntity entitat, MetaNodeEntity metaNode) {
		//Una metaDada pot ser de un meta-document, i el evict i les notificacions son per expedients
		if (metaNode != null && metaNode instanceof MetaExpedientEntity) {
			List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
					entitat, 
					(MetaExpedientEntity)metaNode,
					ExpedientEstatEnumDto.OBERT, 
					0);
			for (ExpedientEntity expedient: expedients) {
				cacheHelper.evictErrorsValidacioAndNotify(expedient.getId());
			}
		}
	}
	
	public MetaDadaEntity update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
				entitat,
				metaNodeId);
		MetaDadaEntity entity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDada.getId());

		if (!metaDada.getMultiplicitat().equals(entity.getMultiplicitat())) {
			evictValidacionsExpedients(entitat, metaNode);
		}
		
		entity.update(metaDada);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		
		return entity;
	}
	
	public MetaDadaEntity delete(
			Long entitatId,
			Long metaNodeId,
			Long id, String rolActual, Long organId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(entitat, metaNodeId);		
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(entitat, metaNode, id);
		
		//Eliminar les possibles validacions sobre la dada
		List<MetaExpedientTascaValidacioEntity> validacionsDada = metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
				ItemValidacioTascaEnum.DADA,
				id);
		
		if (validacionsDada!=null && validacionsDada.size()>0) {
			metaExpedientTascaValidacioRepository.deleteAll(validacionsDada);
		}
		
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
		
		evictValidacionsExpedients(entitat, metaNode);		
		
		return metaDada;
	}
	
	public MetaDadaEntity updateActiva(
			Long entitatId,
			Long metaNodeId,
			Long id,
			boolean activa,
			String rolActual,
			Long organId) {
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
		
		return metaDada;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDadaHelper.class);
}