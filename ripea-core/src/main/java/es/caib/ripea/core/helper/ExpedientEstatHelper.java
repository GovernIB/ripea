/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;


@Component
public class ExpedientEstatHelper {
	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;


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