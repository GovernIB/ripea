package es.caib.ripea.service.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;

@Component
public class ExpedientEstatHelper {
	
	@Autowired private ExpedientEstatRepository expedientEstatRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;

	public ExpedientEstatEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedientEntity, String codi) {
		return expedientEstatRepository.findByMetaExpedientAndCodi(metaExpedientEntity, codi);
	}
	
	public ExpedientEstatEntity updateExpedientEstat(
			MetaExpedientEntity metaExpedientEntity,
			ExpedientEstatEntity expEstatEntity,
			Long entitatId,
			ExpedientEstatDto estat, 
			String rolActual, 
			Long organId) {
		expEstatEntity.update(estat.getCodi(), estat.getNom(), estat.getColor(), metaExpedientEntity, estat.getResponsableCodi());
		return expEstatEntity;
	}
	
	public ExpedientEstatDto createExpedientEstat(
			Long entitatId,
			ExpedientEstatDto estat, 
			String rolActual, 
			Long organId) {
		logger.debug("Creant un nou estat d'expedient (" +
				"entitatId=" + entitatId + ", " +
				"estat=" + estat + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, estat.getMetaExpedientId());
		int ordre = expedientEstatRepository.countByMetaExpedient(metaExpedient);
		ExpedientEstatEntity expedientEstat = ExpedientEstatEntity.getBuilder(
				estat.getCodi(),
				estat.getNom(),
				ordre,
				estat.getColor(),
				metaExpedient,
				estat.getResponsableCodi()).
				build();
		//if inicial of the modified state is true set inicial of other states to false
		if(estat.isInicial()){
			List<ExpedientEstatEntity> expedientEstats =  expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient);
			for (ExpedientEstatEntity expEst: expedientEstats){
				if(!expEst.equals(expedientEstat)){
					expEst.updateInicial(false);
				}
			}
			expedientEstat.updateInicial(true);
		} else {
			expedientEstat.updateInicial(false);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(
				expedientEstatRepository.save(expedientEstat),
				ExpedientEstatDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientEstatHelper.class);
	

}
