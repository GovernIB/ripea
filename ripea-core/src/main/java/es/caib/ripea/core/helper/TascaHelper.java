package es.caib.ripea.core.helper;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.ExpedientTascaRepository;

@Component
public class TascaHelper {
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	
	public boolean shouldNotifyAboutDeadline(Date dataLimit) {
		boolean shouldNotifyAboutDeadline = false;
		if (dataLimit != null) {
			int preavisDataLimitEnDies = configHelper.getAsInt("es.caib.ripea.tasca.preavisDataLimitEnDies");
			if ((new Date()).after(new DateTime(dataLimit).minusDays(preavisDataLimitEnDies).toDate())) {
				shouldNotifyAboutDeadline = true;
			}
		}
		return shouldNotifyAboutDeadline;
	}
	
	public ExpedientTascaEntity comprovarTasca(Long expedientTascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariActual = usuariHelper.getUsuariByCodiDades(auth.getName());
		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
		
		if (tasca == null)
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		
		UsuariEntity responsableActual = tasca.getResponsableActual();
		
		if (responsableActual != null && ! responsableActual.equals(usuariActual))
			throw new ValidationException(tasca.getId(), ExpedientTascaEntity.class, "La tasca s'està tramitant per un altre usuari (codi=" + responsableActual.getCodi() + ")");
		
		return tasca;
	}

}
