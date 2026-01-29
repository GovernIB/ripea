package es.caib.ripea.service.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;

@Component
public class ExpedientEstatHelper {
	
	@Autowired private ExpedientEstatRepository expedientEstatRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ExpedientRepository expedientRepository;

	public ExpedientEstatEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedientEntity, String codi) {
		return expedientEstatRepository.findByMetaExpedientAndCodi(metaExpedientEntity, codi);
	}
	
	public ExpedientEstatEntity moveTo(
			Long entitatId,
			Long metaExpedientId,
			Long expedientEstatId,
			int posicio, String rolActual) throws NotFoundException {
		ExpedientEstatEntity estat = expedientEstatRepository.getOne(expedientEstatId);

		List<ExpedientEstatEntity> estats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
				estat.getMetaExpedient());
		
		int anteriorIndex = -1; 
		for (int i = 0; i < estats.size(); i++) {
			if (estats.get(i).getId().equals(estat.getId())) {
				anteriorIndex = i;
				break;
			}
		}
		estats.add(
				posicio,
				estats.remove(anteriorIndex));
		for (int i = 0; i < estats.size(); i++) {
			estats.get(i).updateOrdre(i);
		}
		
		return estat;
	}
	
	public ExpedientEntity updateEstatAdditional(Long entitatId, Long expedientId, Long estatId) {
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				true,
				false,
				false,
				null);
		
		entityComprovarHelper.comprovarEstatExpedient(entitatId, expedientId, ExpedientEstatEnumDto.OBERT);
		
		ExpedientEstatEntity estat;
		if (estatId != null) {
			estat = expedientEstatRepository.getOne(estatId);
		} else { // if it is null it means that "OBERT" state was choosen
			estat = null;
		}
		String codiEstatAnterior;
		if (expedient.getEstatAdditional() != null) {
			codiEstatAnterior = expedient.getEstatAdditional().getCodi();
		} else {
			codiEstatAnterior = messageHelper.getMessage("expedient.estat.enum.OBERT");
		}
		expedient.updateEstatAdditional(
				estat);
		// log change of state
		String codiEstatNou;
		if (expedient.getEstatAdditional() != null) {
			codiEstatNou = expedient.getEstatAdditional().getCodi();
		} else {
			codiEstatNou = messageHelper.getMessage("expedient.estat.enum.OBERT");
		}
		if(!codiEstatAnterior.equals(codiEstatNou)){
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.CANVI_ESTAT,
					codiEstatAnterior,
					codiEstatNou,
					false,
					false);
		}
		
		// if new state has usuari responsable agafar by this user
		if (estat != null && estat.getResponsableCodi() != null) {
			agafarByUserWithCodi(
					entitatId, 
					expedientId,
					estat.getResponsableCodi());
		}
		
		return expedient;
	}
	
	private void agafarByUserWithCodi(
			Long entitatId,
			Long expedientId,
			String codi) {
		logger.debug("Agafant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "usuari=" + codi + ")");
		ExpedientEntity expedient = expedientRepository.getOne(expedientId);

		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(codi);
		
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
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
