/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.ExpedientSeguidorService;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar els seguidors dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientSeguidorServiceImpl implements ExpedientSeguidorService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
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
		
		UsuariEntity usuariActual = usuariRepository.findByCodi(auth.getName());
		if (!expedient.getSeguidors().contains(usuariActual)) 
//			throw new ValidationException("L'usuari actual " + usuariActual.getCodi() + " ja és seguidor de l'expedient seleccionat");
			expedient.addSeguidor(usuariActual);
		
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
		
		UsuariEntity usuariActual = usuariRepository.findByCodi(auth.getName());
		if (expedient.getSeguidors().contains(usuariActual))
//			throw new ValidationException("L'usuari actual " + usuariActual.getCodi() + " no és seguidor de l'expedient seleccionat");
			expedient.getSeguidors().remove(usuariActual);
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
