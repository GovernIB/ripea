package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.ExpedientSeguidorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpedientSeguidorServiceImpl implements ExpedientSeguidorService {

	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ExpedientHelper expedientHelper;
	
	@Override
	@Transactional
	public void follow(
			Long entitatId,
			Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Afegint nou seguidor a l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "seguidor=" + auth.getName() + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId, 
				false, 
				false, 
				false,
				false,
				false,
				null);

		expedientHelper.follow(expedient, auth.getName());
	}
	
	@Override
	@Transactional
	public void unfollow(Long entitatId, Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Deixant de seguir l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "seguidor=" + auth.getName() + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId, 
				false, 
				false, 
				false,
				false,
				false,
				null);

		expedientHelper.unfollow(expedient, auth.getName());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UsuariDto> getFollowersExpedient(Long entitatId, Long expedientId) {
		logger.debug("Recuperant els seguidors de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId, 
				false, 
				false, 
				false,
				false,
				false,
				null);
		
		return conversioTipusHelper.convertirList(
				expedient.getSeguidors(), 
				UsuariDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientSeguidorServiceImpl.class);

}
