package es.caib.ripea.service.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import io.micrometer.core.instrument.Timer;

@Component
public class MetaDadaHelper {
	
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private ValidacioCacheEvictHelper validacioCacheEvictHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private MetaDocumentHelper metaDocumentHelper;

	public MetaDadaEntity findByMetaNodeAndCodi(MetaNodeEntity metaNode, String codi) {
		return metaDadaRepository.findByMetaNodeAndCodi(metaNode, codi);
	}
	
	public MetaDadaEntity create(Long entitatId, Long metaNodeId, MetaDadaDto metaDada, String rolActual, Long organId) {
		
		Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());
		
		try {
		
			logger.debug("Creant una nova meta-dada (entitatId=" + entitatId + ", metaNodeId=" + metaNodeId + ", metaDada=" + metaDada + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
			MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(entitat, metaNodeId);
			metaDocumentHelper.comprovarPermisModificacioMetaDades(metaNode);
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
	
			MetaDadaEntity metaDadaEntity = MetaDadaEntity.getBuilder(
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
			
			evictValidacionsExpedients(entitat, metaNode, false);
			
			metaDadaEntity = metaDadaRepository.save(metaDadaEntity);
			
			if (metaNode instanceof MetaExpedientEntity) {
				contingutLogHelper.logProcedimentObjecte(
						metaNode.getId(),
						LogTipusEnumDto.MODIFICACIO,
						metaDadaEntity,
						LogObjecteTipusEnumDto.METADADA,
						LogTipusEnumDto.CREACIO,
						metaDadaEntity.getCodi(),
						metaDadaEntity.getNom());
			}
			
			return metaDadaEntity;
			
		} catch (Exception e) {
			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Procediment.metaDada", "resultado", "error");
			throw e;
		}			
	}
	
	//El evict de validacions es fa sempre en segon pla i després del commit de la transacció (veure
	//TransactionAfterCommitUtils i ValidacioCacheEvictHelper). Ja no es notifica per SSE: la validesa es
	//recalcula peresosament quan algú obre o llista el node. Els paràmetres entitat/notificaSse es mantenen
	//per compatibilitat amb els cridadors.
	public void evictValidacionsExpedients(EntitatEntity entitat, MetaNodeEntity metaNode, boolean notificaSse) {
		if (metaNode == null) {
			return;
		}
		if (metaNode instanceof MetaExpedientEntity) {
			//Metadada del meta-expedient: el evict és pels expedients oberts d'aquest procediment
			final Long metaExpedientId = metaNode.getId();
			TransactionAfterCommitUtils.run(() ->
					validacioCacheEvictHelper.evictValidacioExpedientsPerMetaExpedientEnBackground(
							metaExpedientId, ExpedientEstatEnumDto.OBERT));
		} else if (metaNode instanceof MetaDocumentEntity) {
			//Metadada d'un meta-document: el evict és pels documents d'aquest tipus dins expedients oberts
			final Long metaDocumentId = metaNode.getId();
			TransactionAfterCommitUtils.run(() ->
					validacioCacheEvictHelper.evictValidacioDocumentsPerMetaDocumentEnBackground(metaDocumentId));
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
		metaDocumentHelper.comprovarPermisModificacioMetaDades(metaNode);
		MetaDadaEntity metaDadaEntity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				metaDada.getId());

		if (!metaDada.getMultiplicitat().equals(metaDadaEntity.getMultiplicitat())) {
			evictValidacionsExpedients(entitat, metaNode, false);
		}
		
		metaDadaEntity.update(metaDada);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		
		if (metaNode instanceof MetaExpedientEntity) {
			contingutLogHelper.logProcedimentObjecte(
					metaNode.getId(),
					LogTipusEnumDto.MODIFICACIO,
					metaDadaEntity,
					LogObjecteTipusEnumDto.METADADA,
					LogTipusEnumDto.MODIFICACIO,
					metaDadaEntity.getCodi(),
					metaDadaEntity.getNom());
		}
		
		return metaDadaEntity;
	}
	
	public MetaDadaEntity delete(
			Long entitatId,
			Long metaNodeId,
			Long id, String rolActual, Long organId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(entitat, metaNodeId);
		metaDocumentHelper.comprovarPermisModificacioMetaDades(metaNode);
		MetaDadaEntity metaDadaEntity = entityComprovarHelper.comprovarMetaDada(entitat, metaNode, id);

		//Eliminar les possibles validacions sobre la dada
		List<MetaExpedientTascaValidacioEntity> validacionsDada = metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
				ItemValidacioTascaEnum.DADA,
				id);
		
		if (validacionsDada!=null && validacionsDada.size()>0) {
			metaExpedientTascaValidacioRepository.deleteAll(validacionsDada);
		}
		
		metaDadaRepository.delete(metaDadaEntity);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		
		evictValidacionsExpedients(entitat, metaNode, false);		
		
		if (metaNode instanceof MetaExpedientEntity) {
			contingutLogHelper.logProcedimentObjecte(
					metaNode.getId(),
					LogTipusEnumDto.MODIFICACIO,
					null,
					LogObjecteTipusEnumDto.METADADA,
					LogTipusEnumDto.ELIMINACIO,
					metaDadaEntity.getCodi(),
					metaDadaEntity.getNom());
		}
		
		return metaDadaEntity;
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
		metaDocumentHelper.comprovarPermisModificacioMetaDades(metaNode);
		MetaDadaEntity metaDadaEntity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaNode,
				id);
		metaDadaEntity.updateActiva(activa);

		//Només cal refrescar la cache de validacions si la metadada és obligatòria: activar o desactivar-ne
		//una de no obligatòria no canvia la validesa de cap expedient ni document.
		if (metaDadaEntity.getMultiplicitat() != null && metaDadaEntity.getMultiplicitat().esObligatoria()) {
			evictValidacionsExpedients(entitat, metaNode, false);
		}

		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			Long metaExpedientId = null;
			if (metaNode instanceof MetaExpedientEntity) {
				metaExpedientId = metaNode.getId();
			} else if (metaNode instanceof MetaDocumentEntity) {
				metaExpedientId = ((MetaDocumentEntity) metaNode).getMetaExpedient().getId();
			}
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}

		if (metaNode instanceof MetaExpedientEntity) {
			contingutLogHelper.logProcedimentObjecte(
					metaNode.getId(),
					LogTipusEnumDto.MODIFICACIO,
					metaDadaEntity,
					LogObjecteTipusEnumDto.METADADA,
					activa?LogTipusEnumDto.ACTIVACIO:LogTipusEnumDto.DESACTIVACIO,
					metaDadaEntity.getCodi(),
					metaDadaEntity.getNom());
		}
		
		return metaDadaEntity;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDadaHelper.class);
}